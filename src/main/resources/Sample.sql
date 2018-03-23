--Title:<Title for this section> <SQL script must not be multi-folded, it must be written in single line and ended with semi colon>
select <COLUMNS>,COUNT(*) AS "NO OF ITEMS", MAX(SYSTIMESTAMP - STARTDATE) AS "WATINGTIME" FROM <SCHEMA>.<TABLE> WHERE <CONDITIONS> GROUP BY <COLUMNS> ORDER BY <COLUMNS>;

--Title:<Title for this section>
--SubTitle
Query1
--SubTitle
Query2