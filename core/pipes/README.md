# Bounty offered to solve this build error

I'm offering a bounty for anyone who can determine the cause of these kinds of errors when running
"gradle eclipse":

    wya1pi@bob:~/dev/git-repos/platform/Java-Common/integration-tests-common
    -> gradle cleanEclipse eclipse
    :integration-tests-common:cleanEclipseClasspath
    :integration-tests-common:cleanEclipseJdt UP-TO-DATE
    :integration-tests-common:cleanEclipseProject
    :integration-tests-common:cleanEclipse
    :integration-tests-common:eclipseClasspath FAILED

    FAILURE: Build failed with an exception.

    * What went wrong:
    Execution failed for task ':integration-tests-common:eclipseClasspath'.
    > Could not resolve all dependencies for configuration 'detachedConfiguration1'.
       > Module version com.bosch.upa.uhu:integration-tests-common:0.4.0-SNAPSHOT, configuration 'detachedConfiguration1' declares a dependency on configuration 'archives' which is not declared in the module descriptor for com.bosch.upa.uhu:uhu-API:0.4.0-SNAPSHOT
    
In the current project, this is worked around by excluding 'uhu-API' as a transitive dependency.
This shouldn't be necessary. I think this is a bug with the eclipse plugin but it's hard to 
reproduce with a build that doesn't include uhu artifacts/code
