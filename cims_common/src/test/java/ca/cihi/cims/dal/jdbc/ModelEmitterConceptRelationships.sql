with
  elements as (select ec.baseclassificationname as classification,  ec.classid, ec.classname, elementid
    from class ec inner join element ee on ec.classid =ee.classid),
    
  elementversions as (select elements.classification, elements.classid, elements.classname, elements.elementid, elementversionid, versionCode
    from elements inner join elementversion ev on elements.elementid = ev.elementid),

  conceptproperties as (select ev.classification,dev.classname as domainclassname, ev.classname, rev.classname as rangeclassname
    from elementversions ev inner join conceptpropertyversion cpv on ev.elementversionid=cpv.conceptpropertyid
    inner join elementversions rev on rev.elementid=cpv.rangeelementid
    inner join elementversions dev on dev.elementid=cpv.domainelementid)
    
    select distinct * from conceptproperties
    order by classification, rangeclassname