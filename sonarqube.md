## sonar qube

### general 
runtime arguments for gradle task to do analysis in Gradle -> Tasks -> verification -> sonar
`sonar -Dsonar.projectKey=Warhammer40k -Dsonar.projectName='Warhammer40k' -Dsonar.host.url=http://localhost:9000 -Dsonar.token=sqp_a648ec29e9c953af5c1454e51eb93779e253fea0`

### test coverage

sonar will trigger tests and tests will trigger jacoco report

for test coverage sonarqube relies on Jacoco the report can be found at:
 `build/reports/jacoco/test/html/index.html`
and is triggered via gradle task:
Gradle -> Tasks -> verification -> jacocoTestReport

### issues
 null safe operators like this "single ${model?.name ?: "soldier"}"
 cause coverage issue related to redundant nullchecks by the compiler, see https://youtrack.jetbrains.com/issue/KT-52472
 there are only 2 branches model == null and model != null
 in the bytecode we have 4 branches model == null && model.name == null  .....

