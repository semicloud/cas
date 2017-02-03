echo install utils components...
cd /d E:\CodeWorld\IdeaJavaProjects\utils
call mvn clean install
echo utils components install complete...
pause

cd /d E:\CodeWorld\IdeaJavaProjects\cas
echo compile and install and packaging...
call mvn clean install package
echo now deplpy...
set datestr=%date:~0,4%%date:~5,2%%date:~8,2%%time:~0,2%%time:~3,2%%time:~6,2%
set rootDir=deploy_%datestr%
rd /s /q %rootDir%

echo create root_dir %rootDir%
md %rootDir%

echo create conf dirs
md %rootDir%\conf
md %rootDir%\conf\ini
md %rootDir%\conf\xml
md %rootDir%\lib

echo copy jars...
copy cas_alarm\target\cas_alarm-1.0-SNAPSHOT.jar %rootDir%\cas_alarm-1.0-SNAPSHOT.jar
copy cas_scheduler_timepoint\target\cas_scheduler_timepoint-1.0-SNAPSHOT.jar %rootDir%\cas_scheduler_timepoint-1.0-SNAPSHOT.jar
copy cas_scheduler_realtime\target\cas_scheduler_realtime-1.0-SNAPSHOT.jar %rootDir%\cas_scheduler_realtime-1.0-SNAPSHOT.jar

echo copy libraries...,override jars already exited
xcopy cas_alarm\target\lib %rootDir%\lib /E /Y
xcopy cas_scheduler_realtime\target\lib %rootDir%\lib /E /Y
xcopy cas_scheduler_timepoint\target\lib %rootDir%\lib /E /Y

echo copy configuration files...
xcopy cas_shared\src\main\resources\conf\ini %rootDir%\conf\ini /E
xcopy cas_shared\src\main\resources\conf\xml %rootDir%\conf\xml /E

echo copy bat files to %rootDir%...
xcopy bats %rootDir% /E
xcopy README.md %rootDir% /E

echo deploy complete...
explorer %rootDir%

pause
