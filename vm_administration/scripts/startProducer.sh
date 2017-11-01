#!/bin/bash
java -jar producer.jar -w /home/scadmin/clients -s "/usr/bin/scp -i /home/scadmin/id_rsa scadmin@192.168.56.2:" -h 127.0.0.1 -t /home/scadmin/tmp