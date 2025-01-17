create or replace package body cims_data_migration is

procedure init_family(p_version_code varchar2) is
begin
insert into CIMS_CONCEPT_ATTRIBUTE (
version_code,
concept_id,
concept_type_code,
parent_concept_id,
unit_concept_id
)
values (
p_version_code,
0,
'FAM',
null,
null
);

insert into CIMS_DESC (
concept_id,
language_code,
short_desc,
long_desc,
user_desc,
version_code
)
values (
0,
'ENG',
'Family ' || p_version_code,
'Family of Classifications ' || p_version_code,
'Family of Classifications ' || p_version_code,
p_version_code
);

insert into CIMS_DESC (
concept_id,
language_code,
short_desc,
long_desc,
user_desc,
version_code
)
values (
0,
'FRA',
'Family ' || p_version_code,
'Family of Classifications ' || p_version_code,
'Family of Classifications ' || p_version_code,
p_version_code
);

end init_family;

procedure init_data
is
begin
delete from cims_concept;
commit;

insert into CIMS_CONCEPT (
concept_id,
concept_code,
classification_code
)
values (
0,
'FAMILY',
null
);


insert into CIMS_CONCEPT (
concept_id,
concept_code,
classification_code
)
values (
1,
'ICD-10-CA',
'ICD-10-CA'
);

insert into CIMS_CONCEPT (
concept_id,
concept_code,
classification_code
)
values (
11,
'ICD-10-CA_COVER',
'ICD-10-CA'
);

commit;
end init_data;


procedure clean_desc is
td string(32767);

cursor text_desc_cursor is
(select 
concept_id, 
language_code, 
short_desc, 
long_desc, 
user_desc, 
text_desc, 
version_code
from cims_desc d
where d.text_desc is not null)
for update of text_desc nowait;
begin
for r in text_desc_cursor loop
--dbms_output.put_line(r.text_desc);
td := r.text_desc;
td := replace(td, '</qualifierlist>');
td := replace(td, '<qualifierlist type="includes">');
td := replace(td, '<label>');
td := replace(td, '</label>');
td := replace(td, '<qualifierlist type="also">');
td := replace(td, '</also>', '</also><br>');
td := replace(td, '<qualifierlist type="excludes">');
td := replace(td, '</exclude>', '</exclude><br>');
td := replace(td, '<qualifierlist type="note">');
td := replace(td, '</note>', '</note><br>');
td := replace(td, '<include>');
td := replace(td, '</include>', '<br>');
td := replace(td, '<xref refid="', '<a href="#');
td := replace(td, '</xref>', '</a>');
td := replace(td, 'clause>', 'p>');
td := replace(td, 'para>', 'p>');
td := replace(td, 'ulist>', 'ul>');
td := replace(td, 'listitem>', 'li>');
td := replace(td, '<table', '<table border="1"');
td := replace(td, 'colwidth', 'width');
td := replace(td, '<brace', '<table');
td := replace(td, 'brace>', 'table>');
td := replace(td, '<segment', '<td');
td := replace(td, 'segment>', 'td>');

--dbms_output.put_line(td);

update cims_desc
set text_desc = td
where current of text_desc_cursor;

end loop;


commit;
end clean_desc;

procedure migrate_icd_concept is
begin

insert into cims_concept (
concept_id, 
concept_code,
classification_code
)
select
cims_concept_seq.nextval,
concept_code,
'ICD-10-CA' classification_code
from
(
select distinct
trim(c.category_code) concept_code
from
icd.category c
where
c.clinical_classification_code like '10CA%'
);

-- migrate CATEGORY_DETAIL into CIMS_CONCEPT
insert into cims_concept (
concept_id, 
concept_code,
classification_code
)
select
cims_concept_seq.nextval,
concept_code,
'ICD-10-CA' classification_code
from
(
select distinct
trim(c.category_code) || '_' || d.category_detail_type_code concept_code
from
icd.category c,
icd.category_detail d
where
c.category_id = d.category_id
and c.status_code = 'A'
);

-- migrate CATEGORY_TABLE_OUTPUT into CIMS_CONCEPT
insert into cims_concept (
concept_id, 
concept_code,
classification_code
)
select
cims_concept_seq.nextval,
concept_code,
'ICD-10-CA' classification_code
from
(
select distinct
trim(c.category_code) || '-T' concept_code
from
icd.category c,
icd.category_table_output d
where
c.category_id = d.category_id
and c.status_code = 'A'
);

end migrate_icd_concept;

function get_concept_id_from_code(concept_code varchar2) return number
is
cid number;
begin
select
cc.concept_id into cid
from
cims_concept cc
where
cc.concept_code = concept_code
and cc.classification_code = 'ICD-10-CA';
return cid;
end get_concept_id_from_code;

function get_concept_id_from_category(p_category_id number) return number
is
cid number;
begin
select
cc.concept_id into cid
from
cims_concept cc,
icd.category c
where
cc.concept_code = trim(c.category_code)
and cc.classification_code = 'ICD-10-CA'
and c.category_id = p_category_id;
return cid;

exception
when no_data_found then
return null;
when others then
dbms_output.put_line(cid);
end get_concept_id_from_category;

procedure migrate_icd(p_version_code varchar2) is
begin
insert into CIMS_CONCEPT_ATTRIBUTE (
version_code,
concept_id,
concept_type_code,
parent_concept_id,
unit_concept_id
)
values (
p_version_code,
1,
'CLS',
0,
11
);

insert into CIMS_DESC (
concept_id,
language_code,
short_desc,
long_desc,
user_desc,
version_code
)
values (
1,
'ENG',
'ICD-10-CA ' || p_version_code,
'INTERNATIONAL STATISTICAL CLASSIFICATION OF DISEASES AND RELATED HEALTH PROBLEMS TENTH REVISION, CANADA [ICD-10-CA] ' || p_version_code,
'INTERNATIONAL STATISTICAL CLASSIFICATION OF DISEASES AND RELATED HEALTH PROBLEMS TENTH REVISION, CANADA [ICD-10-CA] ' || p_version_code,
p_version_code
);

insert into CIMS_DESC (
concept_id,
language_code,
short_desc,
long_desc,
user_desc,
version_code
)
values (
1,
'FRA',
'ICD-10-CA ' || p_version_code,
'CLASSIFICATION STATISTIQUE INTERNATIONALE DES MALADIES ET DES PROBLÈMES DE SANTÉ CONNEXES DIXIÈME VERSION, CANADA [CIM-10-CA] ' || p_version_code,
'CLASSIFICATION STATISTIQUE INTERNATIONALE DES MALADIES ET DES PROBLÈMES DE SANTÉ CONNEXES DIXIÈME VERSION, CANADA [CIM-10-CA] ' || p_version_code,
p_version_code
);

insert into CIMS_CONCEPT_ATTRIBUTE (
version_code,
concept_id,
concept_type_code,
parent_concept_id,
unit_concept_id
)
values (
p_version_code,
11,
'CLS',
null,
11
);

insert into CIMS_DESC (
concept_id,
language_code,
short_desc,
long_desc,
user_desc,
text_desc,
version_code
)
values (
11,
'ENG',
'ICD-10-CA ' || p_version_code,
'INTERNATIONAL STATISTICAL CLASSIFICATION OF DISEASES AND RELATED HEALTH PROBLEMS TENTH REVISION, CANADA [ICD-10-CA] ' || p_version_code,
'INTERNATIONAL STATISTICAL CLASSIFICATION OF DISEASES AND RELATED HEALTH PROBLEMS TENTH REVISION, CANADA [ICD-10-CA] ' || p_version_code,
'<table border="5" BORDERCOLOR="#33339F" RULES="NONE" FRAME="BOX">
<tr>
 <td>INTERNATIONAL STATISTICAL CLASSIFICATION OF DISEASES AND RELATED HEALTH PROBLEMS TENTH REVISION, CANADA [ICD-10-CA]</td>
</tr>
<tr><td>' || p_version_code || '</td></tr>
</table>
<!-- *** icd banner *** -->
<div><img src="img/icdcci_logo.jpg" alt="ICD-10-CA/CCI Banner"/></div>
(Single Click on Your Selection)
<table border="5" BORDERCOLOR="#800000" BGCOLOR="#33339F" style="color:yellow" width="100%">
	<tr align="left">
		<td width="50%">License Agreement</td><td width="50%">ICD-10-CA Quick Start Guide</td>
	</tr>
	<tr align="left">
		<td>Preface</td><td>Folio Help</td>
	</tr>
	<tr align="left">
		<td>About CIHI</td><td>Introduction to ICD-10-CA</td>
  </tr>
  <tr>
		<td colspan="2" align="center">Contacts</td>
  </tr>
</table>
',
p_version_code);

insert into CIMS_DESC (
concept_id,
language_code,
short_desc,
long_desc,
user_desc,
text_desc,
version_code
)
values (
11,
'FRA',
'CIM-10-CA ' || p_version_code,
'CLASSIFICATION STATISTIQUE INTERNATIONALE DES MALADIES ET DES PROBLÈMES DE SANTÉ CONNEXES DIXIÈME VERSION, CANADA [CIM-10-CA] ' || p_version_code,
'CLASSIFICATION STATISTIQUE INTERNATIONALE DES MALADIES ET DES PROBLÈMES DE SANTÉ CONNEXES DIXIÈME VERSION, CANADA [CIM-10-CA] ' || p_version_code,
'<table border="5" BORDERCOLOR="#33339F" RULES="NONE" FRAME="BOX">
<tr>
 <td>CLASSIFICATION STATISTIQUE INTERNATIONALE DES MALADIES ET DES PROBLÈMES DE SANTÉ CONNEXES DIXIÈME VERSION, CANADA</td>
</tr>
<tr><td>' || p_version_code || '</td></tr>
</table>
<!-- *** icd banner *** -->
<div><img src="img/cimcci_logo.jpg" alt="ICD-10-CA/CCI Banner"/></div>
(Cliquez sur votre sélection)
<table border="5" BORDERCOLOR="#800000" BGCOLOR="#33339F" style="color:yellow" width="100%">
	<tr align="left">
		<td width="50%">Accord de licence</td><td width="50%">CIM-10-CA Mise en route</td>
	</tr>
	<tr align="left">
		<td>Préface</td><td>Aide de Folio</td>
	</tr>
	<tr align="left">
		<td>À propos de l''ICIS</td><td>Introduction à la CIM-10-CA</td>
  </tr>
  <tr>
		<td colspan="2" align="center">Contacts</td>
  </tr>
</table>
',
p_version_code);

commit;

-- migrate CATEGORY data
insert into CIMS_CONCEPT_ATTRIBUTE (
version_code,
concept_id,
concept_type_code,
ca_enhancement_flag,
dagger_asterisk,
parent_concept_id,
unit_concept_id
)
select
p_version_code,
get_concept_id_from_category(c.category_id) concept_id,
c.category_type_code concept_type_code,
decode(c.ca_enhancement_flag, 'Y', '1', 'N', '0') ca_enhancement_flag,
c.dagger_asterisk,
nvl(get_concept_id_from_category(c.parent_category_id), 1) parent_concept_id,
get_concept_id_from_category((select pc.category_id
from icd.category pc
where pc.category_type_code = 'CHP'
connect by prior pc.parent_category_id = pc.category_id
start with pc.category_id = c.category_id)) unit_concept_id
from
icd.category c
where
c.clinical_classification_code = '10CA' || p_version_code
and c.status_code = 'A';

commit;



insert into CIMS_DESC (
concept_id,
language_code,
short_desc,
long_desc,
user_desc,
version_code
)
select
get_concept_id_from_category(c.category_id) concept_id,
'ENG' language_code,
c.short_desc,
c.long_desc,
c.user_desc,
p_version_code
from
icd.category c
where
c.clinical_classification_code = '10CA' || p_version_code
and c.status_code = 'A';

-- migrate CATEGORY French data
insert into CIMS_DESC (
concept_id,
language_code,
short_desc,
long_desc,
user_desc,
version_code
)
select
get_concept_id_from_category(c.category_id) concept_id,
'FRA' language_code,
d.short_desc,
d.long_desc,
d.user_desc,
p_version_code
from
icd.category c,
icd.french_category_desc d
where
c.category_id = d.category_id
and c.clinical_classification_code = '10CA' || p_version_code
and c.status_code = 'A';

commit;


-- migrate CATEGORY_DETAIL data
insert into CIMS_CONCEPT_ATTRIBUTE (
version_code,
concept_id,
concept_type_code,
parent_concept_id
)
select distinct
p_version_code,
cc.concept_id,
decode(d.category_detail_type_code, 'N', 'NOTE', 'I', 'INCLUDE', 'A', 'CODE_ALSO', 'E', 'EXCLUDE', 'O', 'OMIT_CODE') concept_type_code,
get_concept_id_from_category(c.category_id) parent_concept_id
from
icd.category c,
icd.category_detail d,
cims_concept cc
where
c.category_id = d.category_id
and c.clinical_classification_code = '10CA' || p_version_code
and c.status_code = 'A'
and cc.concept_code = trim(c.category_code) || '_' || d.category_detail_type_code
;
commit;

insert into CIMS_DESC (
version_code, 
concept_id, 
language_code, 
text_desc
)
select
p_version_code,
cc.concept_id,
d.language_code,
d.category_detail_data
from
icd.category c,
icd.category_detail d,
cims_concept cc
where
c.category_id = d.category_id
and c.clinical_classification_code = '10CA' || p_version_code
and c.status_code = 'A'
and cc.concept_code = trim(c.category_code) || '_' || d.category_detail_type_code
;

commit;

-- migrate CATEGORY_TABLE_OUTPUT data
insert into CIMS_CONCEPT_ATTRIBUTE (
version_code,
concept_id,
concept_type_code,
parent_concept_id
)
select distinct
p_version_code,
cc.concept_id,
'TEXT' concept_type_code,
get_concept_id_from_category(c.category_id) parent_concept_id
from
icd.category c,
icd.category_table_output d,
cims_concept cc
where
c.category_id = d.category_id
and c.clinical_classification_code = '10CA' || p_version_code
and c.status_code = 'A'
and cc.concept_code = trim(c.category_code) || '-T'
;
commit;

insert into CIMS_DESC (
version_code, 
concept_id, 
language_code, 
text_desc
)
select
p_version_code,
cc.concept_id,
d.language_code,
d.category_table_output_data
from
icd.category c,
icd.category_table_output d,
cims_concept cc
where
c.category_id = d.category_id
and c.clinical_classification_code = '10CA' || p_version_code
and c.status_code = 'A'
and cc.concept_code = trim(c.category_code) || '-T'
;
commit;

end migrate_icd;

procedure migrate_data
is
begin
init_data;
migrate_icd_concept;
init_family('2009');
migrate_icd('2009');
init_family('2012');
migrate_icd('2012');
--clean_desc;
end migrate_data;


end cims_data_migration;
/

