# WifisRecords
Collects WiFi scans. Save measurements in csv file.

<img src="/screenshots/device-2016-03-25-131814.png" width="30%" height="30%" alt="Records list"/>
<img src="/screenshots/device-2016-03-25-131900.png" width="30%" height="30%" alt="New record setup"/>
<img src="/screenshots/device-2016-03-25-131936.png" width="30%" height="30%" alt="Recording"/>

##New record setup:
**Comment** will be written to file name

**Measures per counter tick**. At each measure the _number_ on the recording screen will be updated (incremented). This notice is for  researcher to move to the next point (make a step). Each measure contain the mentioned number of active scans.

**Beep on ...** Will produce beep on every scan(tick), or on every 0.5 second or so on.

csv files are saved in /storage/emulated/0/WifiRecords

filename format: \<datetime\> \<Measures per counter tick\> \<comment\>.csv
