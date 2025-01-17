package ca.cihi.cims.bll.hg;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ca.cihi.cims.bll.query.EqCriterion;
import ca.cihi.cims.bll.query.FindCriterion;
import ca.cihi.cims.bll.query.LikeCriterion;
import ca.cihi.cims.bll.query.Link;
import ca.cihi.cims.bll.query.LinkTrans;
import ca.cihi.cims.bll.query.Ref;
import ca.cihi.cims.bll.query.WrapperCriterion;
import ca.cihi.cims.bll.query.WrapperPropertyCriterion;
import ca.cihi.cims.dal.ConceptVersion;
import ca.cihi.cims.dal.query.ClassIn;
import ca.cihi.cims.dal.query.ElementRef;
import ca.cihi.cims.dal.query.FieldEq;
import ca.cihi.cims.dal.query.Fieldike;
import ca.cihi.cims.dal.query.PointsToElement;
import ca.cihi.cims.dal.query.Restriction;
import ca.cihi.cims.dal.query.TransitiveLink;
import ca.cihi.cims.hg.mapper.config.ClassNamePropertyConfig;
import ca.cihi.cims.hg.mapper.config.ConceptPropertyConfig;
import ca.cihi.cims.hg.mapper.config.MappingConfig;
import ca.cihi.cims.hg.mapper.config.PropertyConfig;
import ca.cihi.cims.hg.mapper.config.PropertyElementConfig;
import ca.cihi.cims.hg.mapper.config.StatusPropertyConfig;
import ca.cihi.cims.hg.mapper.config.WrapperConfig;

/**
 * This object takes a BLL-level wrapper query expressed as Ref and
 * FindCriterion objects, and converts it to a lower-level query for
 * ElementOperations, based on {@link ElementRef} and {@link Restriction}.
 */
public class ElementQueryAssembler {
	// private Wrapper targetWrapper;
	private Collection<FindCriterion> criteria;
	private MappingConfig mappingConfig;

	private Map<Ref, ElementRef> wrapperRefs = new HashMap<Ref, ElementRef>();
	private Map<PropertyKey, ElementRef> propertyRefs = new HashMap<PropertyKey, ElementRef>();

	private ElementRef targetElement;
	List<Restriction> restrictions = new ArrayList<Restriction>();

	public ElementQueryAssembler(Ref targetWrapper, Collection<FindCriterion> criteria, MappingConfig mappingConfig) {
		this.criteria = criteria;
		this.mappingConfig = mappingConfig;

		buildQuery(targetWrapper, criteria);
	}

	private void buildQuery(Ref targetWrapper, Collection<FindCriterion> criteria) {
		targetElement = elementRef(targetWrapper);

		for (FindCriterion criterion : criteria) {

			if (criterion instanceof WrapperCriterion) {
				WrapperCriterion wc = (WrapperCriterion) criterion;

				Ref wrapper = wc.getWrapper();
				ElementRef elementRef = elementRef(wrapper);

				if (wc instanceof WrapperPropertyCriterion) {

					WrapperPropertyCriterion wpc = (WrapperPropertyCriterion) wc;

					WrapperConfig wrapperConfig = this.mappingConfig.getEntity(wrapper.getWrapperClass());

					String property = wpc.getProperty();

					PropertyConfig propCfg = wrapperConfig.getProperty(property);

					if (property.equals("elementId")) {

						add(new FieldEq(elementRef, "elementId", ((EqCriterion) wc).getValue()));

					} else if (propCfg == null) {
						throw new IllegalArgumentException("No such property: "
										+ wrapperConfig.getWrapperClass().getSimpleName() + "." + property);
					}

					if (propCfg instanceof StatusPropertyConfig) {
						add(new FieldEq(targetElement, "status", ((EqCriterion) wc).getValue()));
					} else if (propCfg instanceof ClassNamePropertyConfig) {
						add(new ClassIn(elementRef, (String) ((EqCriterion) wc).getValue()));
					} else if (propCfg instanceof PropertyElementConfig) {

						PropertyElementConfig wrapperPropConfig = (PropertyElementConfig) wrapperConfig
										.getProperty(property);

						ElementRef propElement = propertyElement(wrapper, elementRef, property, wrapperPropConfig);

						if (wpc instanceof EqCriterion) {
							add(new FieldEq(propElement, "value", ((EqCriterion) wpc).getValue()));

						} else if (wpc instanceof LikeCriterion) {
							add(new Fieldike(propElement, "value", ((LikeCriterion) wpc).getLikeExpression()));

						} else if (propCfg instanceof ConceptPropertyConfig) {
							ConceptPropertyConfig conceptPropCfg = (ConceptPropertyConfig) propCfg;

							if (wpc instanceof Link) {
								Link link = (Link) wpc;

								String pointingField = conceptPropCfg.isInverse() ? "domainElementId"
												: "rangeElementId";

								if (link.getTargetElementId() != null) {

									add(new FieldEq(propElement, pointingField, link.getTargetElementId()));

								} else if (link.getTargetWrapper() != null) {

									add(new PointsToElement(propElement, pointingField,
													elementRef(link.getTargetWrapper())));
								}

							} else if (wpc instanceof LinkTrans) {

								LinkTrans linkTrans = (LinkTrans) wpc;

								add(new TransitiveLink(elementRef(linkTrans.getWrapper()),
												conceptPropCfg.getPropertyElementClassName(),
												conceptPropCfg.isInverse(), elementRef(linkTrans.getTargetWrapper())));

							}
						}
					}
				}
			}
		}
	}

	private ElementRef propertyElement(Ref wrapper, ElementRef owner, String propertyName,
					PropertyElementConfig wrapperPropConfig) {

		PropertyKey key = new PropertyKey(wrapper, wrapperPropConfig.getPropertyName());
		if (!propertyRefs.containsKey(key)) {

			ElementRef propertyElmt = new ElementRef(wrapperPropConfig.getPropertyElementClass());
			propertyElmt.setName(wrapperPropConfig.getPropertyName());
			add(new ClassIn(propertyElmt, wrapperPropConfig.getPropertyElementClassName()));

			if (isInverseConceptProperty(wrapperPropConfig)) {
				add(new PointsToElement(propertyElmt, "rangeElementId", owner));
			} else {
				add(new PointsToElement(propertyElmt, "domainElementId", owner));
			}

			propertyRefs.put(key, propertyElmt);
		}
		return propertyRefs.get(key);
	}

	private boolean isInverseConceptProperty(PropertyElementConfig wrapperPropConfig) {
		if (!(wrapperPropConfig instanceof ConceptPropertyConfig)) {
			return false;
		}
		return ((ConceptPropertyConfig) wrapperPropConfig).isInverse();
	}

	private void add(Restriction restriction) {
		restrictions.add(restriction);
	}

	private ElementRef elementRef(Ref wrapper) {
		if (!wrapperRefs.containsKey(wrapper)) {
			ElementRef elementRef = new ElementRef(ConceptVersion.class);
			elementRef.setName(wrapper.getWrapperClass().getSimpleName());
			wrapperRefs.put(wrapper, elementRef);

			WrapperConfig entity = mappingConfig.getEntity(wrapper.getWrapperClass());

			add(new ClassIn(elementRef, entity.getClassNames()));
		}
		return wrapperRefs.get(wrapper);
	}

	public ElementRef getTargetElement() {
		return targetElement;
	}

	public List<Restriction> getRestrictions() {
		return Collections.unmodifiableList(restrictions);
	}

	private class PropertyKey {
		private Ref wrapper;
		private String propertyName;

		public PropertyKey(Ref wrapper, String propertyName) {
			this.wrapper = wrapper;
			this.propertyName = propertyName;
		}

		public Ref getWrapper() {
			return wrapper;
		}

		public String getPropertyName() {
			return propertyName;
		}
	}

}
