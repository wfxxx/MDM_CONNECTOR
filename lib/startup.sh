pid=$(ps -ef | grep "mdm-connector-1.0.jar" | grep -v grep | awk '{print $2}')
echo $pid
kill -9 $pid
nohup java -jar ./mdm-connector-1.0.jar --spring.profiles.active=dev >./mdm-connector.log 2>&1 &
tail -f ./mdm-connector.log
