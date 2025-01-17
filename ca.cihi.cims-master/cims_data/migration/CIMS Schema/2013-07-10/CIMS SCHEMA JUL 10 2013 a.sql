--------------------------------------------------------
-- CIMS SCHEMA 
-- JULY 10, 2013
--------------------------------------------------------

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


CREATE TABLE Concept (
       ConceptID            NUMBER NOT NULL,
       PRIMARY KEY (ConceptID), 
       FOREIGN KEY (ConceptID)
                             REFERENCES ElementVersion
                             ON DELETE CASCADE
);


CREATE TABLE CCIComponentOrAttribute (
       CCIComOrAttID        NUMBER NOT NULL,
       PRIMARY KEY (CCIComOrAttID), 
       FOREIGN KEY (CCIComOrAttID)
                             REFERENCES Concept
                             ON DELETE CASCADE
);


CREATE TABLE CCIConcept (
       ConceptID            NUMBER NOT NULL,
       PRIMARY KEY (ConceptID), 
       FOREIGN KEY (ConceptID)
                             REFERENCES Concept
                             ON DELETE CASCADE
);


CREATE TABLE CCIConceptComponent (
       CCIConceptID         NUMBER NOT NULL,
       CCIComOrAttID        NUMBER NOT NULL,
       PRIMARY KEY (CCIConceptID, CCIComOrAttID), 
       FOREIGN KEY (CCIComOrAttID)
                             REFERENCES CCIComponentOrAttribute
                             ON DELETE CASCADE, 
       FOREIGN KEY (CCIConceptID)
                             REFERENCES CCIConcept
                             ON DELETE CASCADE
);

CREATE INDEX XIF277CCIConceptComponent ON CCIConceptComponent
(
       CCIConceptID                   ASC
);

CREATE INDEX XIF278CCIConceptComponent ON CCIConceptComponent
(
       CCIComOrAttID                  ASC
);


CREATE TABLE PropertyCategory (
       PropertyCategoryID   NUMBER NOT NULL,
       PRIMARY KEY (PropertyCategoryID), 
       FOREIGN KEY (PropertyCategoryID)
                             REFERENCES ElementVersion
                             ON DELETE CASCADE
);


CREATE TABLE Property (
       PropertyID           NUMBER NOT NULL,
       DomainElementID      NUMBER NOT NULL,
       ParentPropertyID     NUMBER NULL,
       PropertyCategoryID   NUMBER NULL,
       PRIMARY KEY (PropertyID), 
       FOREIGN KEY (DomainElementID)
                             REFERENCES Element, 
       FOREIGN KEY (PropertyCategoryID)
                             REFERENCES PropertyCategory, 
       FOREIGN KEY (ParentPropertyID)
                             REFERENCES Property, 
       FOREIGN KEY (PropertyID)
                             REFERENCES ElementVersion
                             ON DELETE CASCADE
);

CREATE INDEX XIF241Property ON Property
(
       ParentPropertyID               ASC
);

CREATE INDEX XIF256Property ON Property
(
       PropertyCategoryID             ASC
);

CREATE INDEX XIF302Property ON Property
(
       DomainElementID                ASC
);


CREATE TABLE DataProperty (
       DataPropertyID       NUMBER NOT NULL,
       IsMetaData           CHAR (1) NOT NULL,
       PRIMARY KEY (DataPropertyID), 
       FOREIGN KEY (DataPropertyID)
                             REFERENCES Property
                             ON DELETE CASCADE
);


CREATE TABLE URLProperty (
       DataPropertyID       NUMBER NOT NULL,
       URL                  VARCHAR2(255) NOT NULL,
       PRIMARY KEY (DataPropertyID), 
       FOREIGN KEY (DataPropertyID)
                             REFERENCES DataProperty
                             ON DELETE CASCADE
);


CREATE TABLE Structure (
       StructureID          NUMBER NOT NULL,
       PRIMARY KEY (StructureID), 
       FOREIGN KEY (StructureID)
                             REFERENCES ElementVersion
                             ON DELETE CASCADE
);


CREATE TABLE StructureElement (
       ElementVersionID     NUMBER NOT NULL,
       StructureID          NUMBER NOT NULL,
       ContextStatus        VARCHAR2(12) NOT NULL,
       ContextStatusDate    DATE NOT NULL,
       Notes                VARCHAR2(255) NULL,
       PRIMARY KEY (ElementVersionID, StructureID), 
       FOREIGN KEY (StructureID)
                             REFERENCES Structure, 
       FOREIGN KEY (ElementVersionID)
                             REFERENCES ElementVersion
                             ON DELETE CASCADE
);

CREATE INDEX XIF257StructureElement ON StructureElement
(
       ElementVersionID               ASC
);

CREATE INDEX XIF296StructureElement ON StructureElement
(
       StructureID                    ASC
);


CREATE TABLE OtherProperty (
       OtherDataPropertyID  NUMBER NOT NULL,
       DataFormat           CHAR(3) NOT NULL,
       DataType             CHAR(3) NOT NULL,
       DataSize             NUMBER NOT NULL,
       DataValue            VARCHAR2(100) NOT NULL,
       PRIMARY KEY (OtherDataPropertyID), 
       FOREIGN KEY (OtherDataPropertyID)
                             REFERENCES DataProperty
                             ON DELETE CASCADE
);


CREATE TABLE DateTimeProperty (
       DateTimePropertyID   NUMBER NOT NULL,
       DateTimeValue        DATE NOT NULL,
       PRIMARY KEY (DateTimePropertyID), 
       FOREIGN KEY (DateTimePropertyID)
                             REFERENCES DataProperty
                             ON DELETE CASCADE
);


CREATE TABLE Language (
       LanguageCode         CHAR(3) NOT NULL,
       LanguageDescription  VARCHAR2(50) NULL,
       PRIMARY KEY (LanguageCode)
);


CREATE TABLE Specialization (
       SpecializationID     NUMBER NOT NULL,
       ParentConceptID      NUMBER NOT NULL,
       ChildConceptID       NUMBER NOT NULL,
       PRIMARY KEY (SpecializationID), 
       FOREIGN KEY (ParentConceptID)
                             REFERENCES Concept
                             ON DELETE CASCADE, 
       FOREIGN KEY (ChildConceptID)
                             REFERENCES Concept
                             ON DELETE CASCADE, 
       FOREIGN KEY (SpecializationID)
                             REFERENCES ElementVersion
                             ON DELETE CASCADE
);

CREATE INDEX XIF149Specialization ON Specialization
(
       ChildConceptID                 ASC
);

CREATE INDEX XIF150Specialization ON Specialization
(
       ParentConceptID                ASC
);


CREATE TABLE XMLProperty (
       XMLPropertyID        NUMBER NOT NULL,
       LanguageCode         CHAR(3) NOT NULL,
       XMLSchemaURL         VARCHAR2(255) NOT NULL,
       XMLText            XMLType NOT NULL,
       PRIMARY KEY (XMLPropertyID), 
       FOREIGN KEY (XMLPropertyID)
                             REFERENCES DataProperty
                             ON DELETE CASCADE, 
       FOREIGN KEY (LanguageCode)
                             REFERENCES Language
);

CREATE INDEX XIF183XMLProperty ON XMLProperty
(
       LanguageCode                   ASC
);


CREATE TABLE GraphicsProperty (
       GraphicsPropertyID   NUMBER NOT NULL,
       LanguageCode         CHAR(3) NOT NULL,
       GraphicsBLOBValue    BLOB NOT NULL,
       GraphicFormat        CHAR(3) NOT NULL,
       PRIMARY KEY (GraphicsPropertyID), 
       FOREIGN KEY (GraphicsPropertyID)
                             REFERENCES DataProperty
                             ON DELETE CASCADE, 
       FOREIGN KEY (LanguageCode)
                             REFERENCES Language
);

CREATE INDEX XIF181GraphicsProperty ON GraphicsProperty
(
       LanguageCode                   ASC
);


CREATE TABLE ConceptProperty (
       ConceptPropertyID    NUMBER NOT NULL,
       RangeElementID       NUMBER NOT NULL,
       InverseConceptPropertyID NUMBER NULL,
       PRIMARY KEY (ConceptPropertyID), 
       FOREIGN KEY (RangeElementID)
                             REFERENCES Element, 
       FOREIGN KEY (InverseConceptPropertyID)
                             REFERENCES ConceptProperty, 
       FOREIGN KEY (ConceptPropertyID)
                             REFERENCES Property
                             ON DELETE CASCADE
);

CREATE INDEX XIF139ConceptProperty ON ConceptProperty
(
       InverseConceptPropertyID       ASC
);

CREATE INDEX XIF301ConceptProperty ON ConceptProperty
(
       RangeElementID                 ASC
);


CREATE TABLE ValidationRule (
       RuleID               NUMBER NOT NULL,
       PRIMARY KEY (RuleID), 
       FOREIGN KEY (RuleID)
                             REFERENCES ElementVersion
                             ON DELETE CASCADE
);


CREATE TABLE TextProperty (
       TextPropertyID       NUMBER NOT NULL,
       LanguageCode         CHAR(3) NOT NULL,
       TextType             CHAR(3) NOT NULL,
       Text                 VARCHAR2(255) NOT NULL,
       PRIMARY KEY (TextPropertyID), 
       FOREIGN KEY (TextPropertyID)
                             REFERENCES DataProperty
                             ON DELETE CASCADE, 
       FOREIGN KEY (LanguageCode)
                             REFERENCES Language
);

CREATE INDEX XIF182TextProperty ON TextProperty
(
       LanguageCode                   ASC
);


CREATE TABLE NumericProperty (
       NumericPropertyID    NUMBER NOT NULL,
       NumericFormat        CHAR(3) NOT NULL,
       NumericValue         NUMBER NOT NULL,
       PRIMARY KEY (NumericPropertyID), 
       FOREIGN KEY (NumericPropertyID)
                             REFERENCES DataProperty
                             ON DELETE CASCADE
);


CREATE TABLE EnumeratedProperty (
       EnumeratedPropertyID NUMBER NOT NULL,
       PRIMARY KEY (EnumeratedPropertyID), 
       FOREIGN KEY (EnumeratedPropertyID)
                             REFERENCES DataProperty
                             ON DELETE CASCADE
);


CREATE TABLE ValueDomain (
       DomainID             NUMBER NOT NULL,
       PRIMARY KEY (DomainID), 
       FOREIGN KEY (DomainID)
                             REFERENCES ElementVersion
                             ON DELETE CASCADE
);


CREATE TABLE Enumeration (
       DomainID             NUMBER NOT NULL,
       DomainValueID        NUMBER NOT NULL,
       LanguageCode         CHAR(3) NOT NULL,
       MinNumericValue      NUMBER NULL,
       MaxNumericValue      NUMBER NULL,
       LiteralValue         VARCHAR2(50) NULL,
       Description          VARCHAR2(255) NULL,
       PRIMARY KEY (DomainID, DomainValueID), 
       FOREIGN KEY (DomainID)
                             REFERENCES ValueDomain
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


CREATE TABLE EnumerationPropertyValue (
       EnumeratedPropertyID NUMBER NOT NULL,
       DomainID             NUMBER NOT NULL,
       DomainValueID        NUMBER NOT NULL,
       PRIMARY KEY (EnumeratedPropertyID, DomainID, DomainValueID), 
       FOREIGN KEY (DomainID, DomainValueID)
                             REFERENCES Enumeration
                             ON DELETE CASCADE, 
       FOREIGN KEY (EnumeratedPropertyID)
                             REFERENCES EnumeratedProperty
                             ON DELETE CASCADE
);

CREATE INDEX XIF210EnumerationPropertyValue ON EnumerationPropertyValue
(
       EnumeratedPropertyID           ASC
);

CREATE INDEX XIF211EnumerationPropertyValue ON EnumerationPropertyValue
(
       DomainValueID                  ASC,
       DomainID                       ASC
);


