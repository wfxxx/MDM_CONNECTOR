pid=$(ps -ef | grep "connecotor-0.0.1-SNAPSHOT.jar" | grep -v grep | awk '{print $2}')
echo $pid
kill -9 $pi
nohup java -jar /esb/mdm-connector/connecotor-0.0.1-SNAPSHOT.jar --spring.profiles.active=dev >/esb/mdm-connector/mdm-connector.log 2>&1 &
tail -f /esb/mdm-connector/mdm-connector.log
