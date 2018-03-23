# ScheduleReport
Simple HTML based email report for the SQLs (with some custom validations) - Sample file is available in resources folder.

Following dependency need to be resolved for successful execution:

1.Supplying the property file location during runtime. <TYPE1> - Refer the properties file for more details on how to define this label.

E.g. java -jar -Dcom.schedulereport.monitorconfig={path/to}/system.properties -Dmonitor.types=<TYPE1> {path/to}/ScheduleReport.jar &

2.Specifying the SQL file location in the properties file along with specific SQL connection details for each files.

Using crontab this shall be scheduled to be executed for every 2 hours, with following sample settings:

00 01,03,05,07,09,11,13,15,17,19,21,23 * * *  java -jar -Dcom.schedulereport.monitorconfig={path/to}/system.properties -Dmonitor.types=<TYPE1> {path/to}/ScheduleReport.jar &
