create or replace procedure cims.migrate_icd is
td string(32767);
begin
delete from cims_concept;
commit;

insert into CIMS_CONCEPT (
concept_id,
concept_code,
concept_type_code,
parent_concept_id
)
values (
0,
'FAM',
'FAM',
null
);

insert into CIMS_DESC (
concept_id,
language_code,
short_desc,
long_desc,
user_desc
)
values (
0,
'ENG',
'Family',
'Family of Classifications',
'Family of Classifications'
);

insert into CIMS_CONCEPT (
concept_id,
concept_code,
concept_type_code,
parent_concept_id,
unit_concept_id
)
values (
1,
'ICD-10-CA',
'CLS',
0,
11
);

insert into CIMS_DESC (
concept_id,
language_code,
short_desc,
long_desc,
user_desc
)
values (
1,
'ENG',
'ICD-10-CA 2012',
'INTERNATIONAL STATISTICAL CLASSIFICATION OF DISEASES AND RELATED HEALTH PROBLEMS TENTH REVISION, CANADA [ICD-10-CA] 2012',
'INTERNATIONAL STATISTICAL CLASSIFICATION OF DISEASES AND RELATED HEALTH PROBLEMS TENTH REVISION, CANADA [ICD-10-CA] 2012'
);

insert into CIMS_CONCEPT (
concept_id,
concept_code,
concept_type_code,
parent_concept_id,
unit_concept_id
)
values (
11,
'00',
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
text_desc
)
values (
11,
'ENG',
'ICD-10-CA 2012',
'INTERNATIONAL STATISTICAL CLASSIFICATION OF DISEASES AND RELATED HEALTH PROBLEMS TENTH REVISION, CANADA [ICD-10-CA] 2012',
'INTERNATIONAL STATISTICAL CLASSIFICATION OF DISEASES AND RELATED HEALTH PROBLEMS TENTH REVISION, CANADA [ICD-10-CA] 2012',
'<table border="5" BORDERCOLOR=#33339F RULES=NONE FRAME=BOX>
<tr>
 <td>INTERNATIONAL STATISTICAL CLASSIFICATION OF DISEASES AND RELATED HEALTH PROBLEMS TENTH REVISION, CANADA [ICD-10-CA]</td>
</tr>
<tr><td>2012</td></tr>
</table>
<!-- *** icd banner *** -->
<div><img src="img/icdcci_logo.jpg" alt="ICD-10-CA/CCI Banner"></div>	
(Single Click on Your Selection)
<table border="5" BORDERCOLOR=#800000 BGCOLOR=#33339F style="color:yellow" width="100%"> 
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
		<td colspan=2 align=center>Contacts</td>
  </tr>	
</table>
');



-- migrate CATEGORY data
insert into CIMS_CONCEPT (
concept_id,
concept_code,
concept_type_code,
ca_enhancement_flag,
dagger_asterisk,
parent_concept_id,
unit_concept_id
)
select
c.category_id concept_id,
trim(c.category_code) concept_code,
c.category_type_code concept_type_code,
decode(c.ca_enhancement_flag, 'Y', '1', 'N', '0') ca_enhancement_flag,
c.dagger_asterisk,
nvl(c.parent_category_id, 1) parent_concept_id,
(select pc.category_id
from icd.category pc
where pc.category_type_code = 'CHP'
connect by prior pc.parent_category_id = pc.category_id
start with pc.category_id = c.category_id) unit_concept_id
from
icd.category c
where
c.clinical_classification_code = '10CA2012'
and c.status_code = 'A'
;

insert into CIMS_DESC (
concept_id,
language_code,
short_desc,
long_desc,
user_desc
)
select
c.category_id concept_id,
'ENG' language_code,
c.short_desc,
c.long_desc,
c.user_desc
from
icd.category c
where
c.clinical_classification_code = '10CA2012'
and c.status_code = 'A';

-- migrate CATEGORY French data
insert into CIMS_DESC (
concept_id,
language_code,
short_desc,
long_desc,
user_desc
)
select
c.category_id concept_id,
'FRA' language_code,
d.short_desc,
d.long_desc,
d.user_desc
from
icd.category c,
icd.french_category_desc d
where
c.category_id = d.category_id
and c.clinical_classification_code = '10CA2012'
and c.status_code = 'A';

commit;

-- migrate CATEGORY_DETAIL data
for d in (
select
concept_seq.nextval concept_id,
decode(d.category_detail_type_code, 'N', 'NOTE', 'I', 'INCLUDE', 'A', 'CODE_ALSO', 'E', 'EXCLUDE', 'O', 'OMIT_CODE') concept_type_code,
c.category_id parent_concept_id,
d.language_code,
d.category_detail_data
from
icd.category c,
icd.category_detail d
where
c.category_id = d.category_id
and c.clinical_classification_code = '10CA2012'
and c.status_code = 'A'
) loop

insert into CIMS_CONCEPT (
concept_id,
concept_type_code,
parent_concept_id
)
values (
d.concept_id,
d.concept_type_code,
d.parent_concept_id
);

insert into CIMS_DESC (
concept_id,
language_code,
text_desc
)
values (
d.concept_id,
d.language_code,
d.category_detail_data
);
end loop;

commit;

-- migrate CATEGORY_TABLE_OUTPUT data
for d in (
select
concept_seq.nextval concept_id,
'TABLE' concept_type_code,
c.category_id parent_concept_id,
d.language_code,
d.category_table_output_data
from
icd.category c,
icd.category_table_output d
where
c.category_id = d.category_id
and c.clinical_classification_code = '10CA2012'
and c.status_code = 'A'
) loop

insert into CIMS_CONCEPT (
concept_id,
concept_type_code,
parent_concept_id
)
values (
d.concept_id,
d.concept_type_code,
d.parent_concept_id
);

insert into CIMS_DESC (
concept_id,
language_code,
text_desc
)
values (
d.concept_id,
d.language_code,
d.category_table_output_data
);
end loop;

commit;

-- clean data
for r in
(select * from cims_desc d
where d.text_desc is not null) loop
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
where concept_id = r.concept_id;

end loop;


commit;

end migrate_icd;
/

