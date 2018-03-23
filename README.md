# ScheduleReport
Simple HTML based email report for the SQLs (with some custom validations)

Following dependency need to be resolved for successful execution:
1.Supplying the property file location during runtime.

E.g. java -jar -Dcom.schedulereport.monitorconfig={path/to}/system.properties {path/to}/ScheduleReport.jar &

2.Specifying the SQL file location in the properties file along with specific SQL connection details for each files.
