
CREATE TABLE Language (
       LanguageCode         CHAR (3) NOT NULL,
       LanguageDescription  VARCHAR2(50) NULL,
       PRIMARY KEY (LanguageCode)
);


CREATE TABLE Class (
       ClassID              NUMBER NOT NULL,
       TableName            VARCHAR2(30) NOT NULL,
       BaseClassificationName VARCHAR2(30) NULL,
       ClassName            VARCHAR2(50) NOT NULL,
       Notes                VARCHAR2(255) NULL,
       PRIMARY KEY (ClassID)
);

CREATE UNIQUE INDEX XAK1Class ON Class
(
       BaseClassificationName         ASC,
       ClassName                      ASC
);


CREATE TABLE Element (
       ElementID            NUMBER NOT NULL,
       ClassID              NUMBER NOT NULL,
       ElementUUID          VARCHAR2(36) NOT NULL,
       Notes                VARCHAR2(255) NULL,
       PRIMARY KEY (ElementID), 
       FOREIGN KEY (ClassID)
                             REFERENCES Class
);

CREATE UNIQUE INDEX XAK1Element ON Element
(
       ElementUUID                    ASC
);

CREATE INDEX XIF294Element ON Element
(
       ClassID                        ASC
);


CREATE TABLE ElementVersion (
       ElementVersionID     NUMBER NOT NULL,
       ElementID            NUMBER NOT NULL,
       VersionCode          VARCHAR2(30) NULL,
       VersionTimestamp     TIMESTAMP NOT NULL,
       Status               VARCHAR2(12) NOT NULL,
       Notes                VARCHAR2(255) NULL,
       PRIMARY KEY (ElementVersionID), 
       FOREIGN KEY (ElementID)
                             REFERENCES Element
);

CREATE INDEX XIF295ElementVersion ON ElementVersion
(
       ElementID                      ASC
);


CREATE TABLE PropertyCategoryVersion (
       PropertyCategoryID   NUMBER NOT NULL,
       PRIMARY KEY (PropertyCategoryID), 
       FOREIGN KEY (PropertyCategoryID)
                             REFERENCES ElementVersion
                             ON DELETE CASCADE
);


CREATE TABLE PropertyVersion (
       PropertyID           NUMBER NOT NULL,
       DomainElementID      NUMBER NOT NULL,
       ParentPropertyID     NUMBER NULL,
       PropertyCategoryID   NUMBER NULL,
       PRIMARY KEY (PropertyID), 
       FOREIGN KEY (DomainElementID)
                             REFERENCES Element, 
       FOREIGN KEY (PropertyCategoryID)
                             REFERENCES PropertyCategoryVersion, 
       FOREIGN KEY (ParentPropertyID)
                             REFERENCES PropertyVersion, 
       FOREIGN KEY (PropertyID)
                             REFERENCES ElementVersion
                             ON DELETE CASCADE
);

CREATE INDEX XIF241PropertyVersion ON PropertyVersion
(
       ParentPropertyID               ASC
);

CREATE INDEX XIF256PropertyVersion ON PropertyVersion
(
       PropertyCategoryID             ASC
);

CREATE INDEX XIF302PropertyVersion ON PropertyVersion
(
       DomainElementID                ASC
);


CREATE TABLE DataPropertyVersion (
       DataPropertyID       NUMBER NOT NULL,
       IsMetaData           CHAR(1) NOT NULL,
       PRIMARY KEY (DataPropertyID), 
       FOREIGN KEY (DataPropertyID)
                             REFERENCES PropertyVersion
                             ON DELETE CASCADE
);


CREATE TABLE HTMLPropertyVersion (
       HTMLPropertyID       NUMBER NOT NULL,
       LanguageCode         CHAR(3) NULL,
       HTMLText             CLOB NOT NULL,
       PRIMARY KEY (HTMLPropertyID), 
       FOREIGN KEY (LanguageCode)
                             REFERENCES Language, 
       FOREIGN KEY (HTMLPropertyID)
                             REFERENCES DataPropertyVersion
                             ON DELETE CASCADE
);

CREATE INDEX XIF314HTMLPropertyVersion ON HTMLPropertyVersion
(
       LanguageCode                   ASC
);


CREATE TABLE BooleanPropertyVersion (
       BooleanPropertyID    NUMBER NOT NULL,
       BooleanValue         CHAR(1) NULL,
       PRIMARY KEY (BooleanPropertyID), 
       FOREIGN KEY (BooleanPropertyID)
                             REFERENCES DataPropertyVersion
                             ON DELETE CASCADE
);


CREATE TABLE ValueDomainVersion (
       DomainID             NUMBER NOT NULL,
       PRIMARY KEY (DomainID), 
       FOREIGN KEY (DomainID)
                             REFERENCES ElementVersion
                             ON DELETE CASCADE
);


CREATE TABLE Enumeration (
       DomainID             NUMBER NOT NULL,
       DomainValueID        NUMBER NOT NULL,
       LanguageCode         CHAR (3) NULL,
       MinNumericValue      NUMBER NULL,
       MaxNumericValue      NUMBER NULL,
       LiteralValue         VARCHAR2(50) NULL,
       Description          VARCHAR2(255) NULL,
       PRIMARY KEY (DomainID, DomainValueID), 
       FOREIGN KEY (DomainID)
                             REFERENCES ValueDomainVersion
                             ON DELETE CASCADE, 
       FOREIGN KEY (LanguageCode)
                             REFERENCES Language
);

CREATE INDEX XIF180Enumeration ON Enumeration
(
       LanguageCode                   ASC
);

CREATE INDEX XIF208Enumeration ON Enumeration
(
       DomainID                       ASC
);


CREATE TABLE EnumeratedPropertyVersion (
       EnumeratedPropertyID NUMBER NOT NULL,
       DomainID             NUMBER NULL,
       DomainValueID        NUMBER NULL,
       PRIMARY KEY (EnumeratedPropertyID), 
       FOREIGN KEY (DomainID, DomainValueID)
                             REFERENCES Enumeration, 
       FOREIGN KEY (EnumeratedPropertyID)
                             REFERENCES DataPropertyVersion
                             ON DELETE CASCADE
);

CREATE INDEX XIF315EnumeratedPropertyVersio ON EnumeratedPropertyVersion
(
       DomainID                       ASC,
       DomainValueID                  ASC
);


CREATE TABLE NumericPropertyVersion (
       NumericPropertyID    NUMBER NOT NULL,
       NumericFormat        CHAR(3) NOT NULL,
       NumericValue         NUMBER NOT NULL,
       PRIMARY KEY (NumericPropertyID), 
       FOREIGN KEY (NumericPropertyID)
                             REFERENCES DataPropertyVersion
                             ON DELETE CASCADE
);


CREATE TABLE TextPropertyVersion (
       TextPropertyID       NUMBER NOT NULL,
       LanguageCode         CHAR (3) NULL,
       TextType             CHAR(3) NOT NULL,
       Text                 VARCHAR2(4000) NOT NULL,
       PRIMARY KEY (TextPropertyID), 
       FOREIGN KEY (TextPropertyID)
                             REFERENCES DataPropertyVersion
                             ON DELETE CASCADE, 
       FOREIGN KEY (LanguageCode)
                             REFERENCES Language
);

CREATE INDEX XIF182TextPropertyVersion ON TextPropertyVersion
(
       LanguageCode                   ASC
);


CREATE TABLE ValidationRuleVersion (
       RuleID               NUMBER NOT NULL,
       PRIMARY KEY (RuleID), 
       FOREIGN KEY (RuleID)
                             REFERENCES ElementVersion
                             ON DELETE CASCADE
);


CREATE TABLE StructureVersion (
       StructureID          NUMBER NOT NULL,
       PRIMARY KEY (StructureID), 
       FOREIGN KEY (StructureID)
                             REFERENCES ElementVersion
                             ON DELETE CASCADE
);


CREATE TABLE ConceptPropertyVersion (
       ConceptPropertyID    NUMBER NOT NULL,
       RangeElementID       NUMBER NOT NULL,
       InverseConceptPropertyID NUMBER NULL,
       PRIMARY KEY (ConceptPropertyID), 
       FOREIGN KEY (RangeElementID)
                             REFERENCES Element, 
       FOREIGN KEY (InverseConceptPropertyID)
                             REFERENCES ConceptPropertyVersion, 
       FOREIGN KEY (ConceptPropertyID)
                             REFERENCES PropertyVersion
                             ON DELETE CASCADE
);

CREATE INDEX XIF139ConceptPropertyVersion ON ConceptPropertyVersion
(
       InverseConceptPropertyID       ASC
);

CREATE INDEX XIF301ConceptPropertyVersion ON ConceptPropertyVersion
(
       RangeElementID                 ASC
);


CREATE TABLE GraphicsPropertyVersion (
       GraphicsPropertyID   NUMBER NOT NULL,
       LanguageCode         CHAR (3) NOT NULL,
       GraphicsBLOBValue    BLOB NOT NULL,
       GraphicFormat        CHAR(3) NOT NULL,
       PRIMARY KEY (GraphicsPropertyID), 
       FOREIGN KEY (GraphicsPropertyID)
                             REFERENCES DataPropertyVersion
                             ON DELETE CASCADE, 
       FOREIGN KEY (LanguageCode)
                             REFERENCES Language
);

CREATE INDEX XIF181GraphicsPropertyVersion ON GraphicsPropertyVersion
(
       LanguageCode                   ASC
);


CREATE TABLE ConceptVersion (
       ConceptID            NUMBER NOT NULL,
       PRIMARY KEY (ConceptID), 
       FOREIGN KEY (ConceptID)
                             REFERENCES ElementVersion
                             ON DELETE CASCADE
);


CREATE TABLE XMLPropertyVersion (
       XMLPropertyID        NUMBER NOT NULL,
       LanguageCode         CHAR (3) NOT NULL,
       XMLSchemaURL         VARCHAR2(255) NOT NULL,
       XMLText              CLOB NOT NULL,
       PRIMARY KEY (XMLPropertyID), 
       FOREIGN KEY (XMLPropertyID)
                             REFERENCES DataPropertyVersion
                             ON DELETE CASCADE, 
       FOREIGN KEY (LanguageCode)
                             REFERENCES Language
);

CREATE INDEX XIF183XMLPropertyVersion ON XMLPropertyVersion
(
       LanguageCode                   ASC
);


CREATE TABLE SpecializationVersion (
       SpecializationID     NUMBER NOT NULL,
       ParentConceptID      NUMBER NOT NULL,
       ChildConceptID       NUMBER NOT NULL,
       PRIMARY KEY (SpecializationID), 
       FOREIGN KEY (ParentConceptID)
                             REFERENCES ConceptVersion
                             ON DELETE CASCADE, 
       FOREIGN KEY (ChildConceptID)
                             REFERENCES ConceptVersion
                             ON DELETE CASCADE, 
       FOREIGN KEY (SpecializationID)
                             REFERENCES ElementVersion
                             ON DELETE CASCADE
);

CREATE INDEX XIF149SpecializationVersion ON SpecializationVersion
(
       ChildConceptID                 ASC
);

CREATE INDEX XIF150SpecializationVersion ON SpecializationVersion
(
       ParentConceptID                ASC
);


CREATE TABLE DateTimePropertyVersion (
       DateTimePropertyID   NUMBER NOT NULL,
       DateTimeValue        DATE NOT NULL,
       PRIMARY KEY (DateTimePropertyID), 
       FOREIGN KEY (DateTimePropertyID)
                             REFERENCES DataPropertyVersion
                             ON DELETE CASCADE
);


CREATE TABLE OtherPropertyVersion (
       OtherDataPropertyID  NUMBER NOT NULL,
       DataFormat           CHAR(3) NOT NULL,
       DataType             CHAR(3) NOT NULL,
       DataSize             NUMBER NOT NULL,
       DataValue            VARCHAR2(100) NOT NULL,
       PRIMARY KEY (OtherDataPropertyID), 
       FOREIGN KEY (OtherDataPropertyID)
                             REFERENCES DataPropertyVersion
                             ON DELETE CASCADE
);


CREATE TABLE StructureElementVersion (
       ElementVersionID     NUMBER NOT NULL,
       StructureID          NUMBER NOT NULL,
       ContextStatus        VARCHAR2(12) NULL,
       ContextStatusDate    DATE NULL,
       Notes                VARCHAR2(255) NULL,
       PRIMARY KEY (ElementVersionID, StructureID), 
       FOREIGN KEY (StructureID)
                             REFERENCES StructureVersion, 
       FOREIGN KEY (ElementVersionID)
                             REFERENCES ElementVersion
                             ON DELETE CASCADE
);

CREATE INDEX XIF257StructureElementVersion ON StructureElementVersion
(
       ElementVersionID               ASC
);

CREATE INDEX XIF296StructureElementVersion ON StructureElementVersion
(
       StructureID                    ASC
);


CREATE TABLE URLPropertyVersion (
       DataPropertyID       NUMBER NOT NULL,
       URL                  VARCHAR2(255) NOT NULL,
       PRIMARY KEY (DataPropertyID), 
       FOREIGN KEY (DataPropertyID)
                             REFERENCES DataPropertyVersion
                             ON DELETE CASCADE
);


CREATE TABLE CCIConceptVersion (
       CCIConceptID         NUMBER NOT NULL,
       PRIMARY KEY (CCIConceptID), 
       FOREIGN KEY (CCIConceptID)
                             REFERENCES ConceptVersion
                             ON DELETE CASCADE
);


CREATE TABLE CCIAttributeVersion (
       CCIAttributeID       NUMBER NOT NULL,
       Type                 CHAR(1) NOT NULL,
       PRIMARY KEY (CCIAttributeID), 
       FOREIGN KEY (CCIAttributeID)
                             REFERENCES ElementVersion
                             ON DELETE CASCADE
);


CREATE TABLE CCIComponentVersion (
       CCIComponentID       NUMBER NOT NULL,
       PRIMARY KEY (CCIComponentID), 
       FOREIGN KEY (CCIComponentID)
                             REFERENCES ElementVersion
                             ON DELETE CASCADE
);


CREATE TABLE CCIGenericAttrVersion (
       CCIGenericAttrID     NUMBER NOT NULL,
       PRIMARY KEY (CCIGenericAttrID), 
       FOREIGN KEY (CCIGenericAttrID)
                             REFERENCES CCIAttributeVersion
                             ON DELETE CASCADE
);


CREATE TABLE CCIReferenceValueAttrVersion (
       CCIReferenceValueAttrID NUMBER NOT NULL,
       PRIMARY KEY (CCIReferenceValueAttrID), 
       FOREIGN KEY (CCIReferenceValueAttrID)
                             REFERENCES CCIAttributeVersion
                             ON DELETE CASCADE
);

