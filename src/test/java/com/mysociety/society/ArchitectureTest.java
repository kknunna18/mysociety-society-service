package com.mysociety.society;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
@AnalyzeClasses(packages = "com.mysociety.society", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {
    @ArchTest static final ArchRule domainDoesNotDependOnWeb = noClasses().that().resideInAPackage("..domain..").should().dependOnClassesThat().resideInAPackage("..web..");
}
