with
  elements as (select ec.baseclassificationname as classification, ec.tablename,  ec.classid, ec.classname, elementid
    from class ec inner join element ee on ec.classid =ee.classid),
    
  elementversions as (select elements.classification, elements.classid, elements.classname, elements.tablename, elements.elementid, elementversionid, versionCode
    from elements inner join elementversion ev on elements.elementid = ev.elementid),
    
  properties as (select * from elements
             inner join elementversion ev on elements.elementid = ev.elementid
             inner join propertyversion pv on ev.elementversionid=pv.propertyid),
 
  assocCountsByElement as (
    select ev.classification, ev.elementid, ev.classname as elementclassname,
    properties.classname as propertyclassname --, properties.propertyid, properties.elementid as propertyelementid
    ,
    count(*) as incidences
    from
      elementversions ev inner join properties on ev.elementid=properties.domainelementid
      and ev.versionCode=properties.versionCode
      and properties.tablename not in ('ConceptPropertyVersion')
    group by ev.classification, ev.elementid, ev.classname, properties.classname
  )

--- end query definitions

select classification, elementclassname, propertyclassname, min(incidences), max(incidences)
from assocCountsByElement
group by classification, elementclassname, propertyclassname
order by classification, elementclassname, propertyclassname