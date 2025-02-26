create or replace view cims.cims_concept_navigation as
select
  c.concept_id,
  c.concept_code,
  c.concept_type_code,
  t.concept_type_desc,
  c.concept_short_desc,
  c.concept_long_desc,
  c.parent_concept_id,
  c.ca_enhancement_flag,
  c.dagger_asterisk,
  t.concept_type_seq_num
from
  cims_concept c,
  cims_concept_type t
where
  c.concept_type_code = t.concept_type_code
  and c.concept_type_code not in ('CODE_ALSO', 'EXCLUDE', 'INCLUDE', 'NOTE', 'OMIT_CODE', 'TABLE')
/

