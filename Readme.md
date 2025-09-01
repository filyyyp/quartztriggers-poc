Pre overenie aktualnych triggerov je dostupny actuator
http://localhost:8080/actuator/quartz/jobs/DEFAULT/SchedulingProductExpirationJob
http://localhost:8080/actuator/quartz/jobs/DEFAULT/ProductExpirationJob

[//]: # (TODO zistit kolko triggerov je v missfire)
[//]: # (TODO zistik kolko triggerov je naplanovanych)


SELECT trigger_name, job_name, next_fire_time, to_timestamp(qrtz_triggers.next_fire_time / 1000.0)
from  qrtz_triggers
WHERE
to_timestamp(next_fire_time / 1000) < now()
ORDER BY qrtz_triggers.next_fire_time ASC


Problemy ktore je potrebne vyriesit
 - zabezpecit aby sa neplanoval opakovane dalsi trigger na produkt na ktory uz je vytvoreny trigger
 - zabezpecit aby sa neplanoval trigger na produkt na ktory sa uz odoslal trigger na spracovanie
 - ako sa vysporiadat s viacerymi replikami vs triggre
 - ak sa bude pouzivat jdbc pre quartz spravit custom sql script na vytahovanie triggerov (overit este implementaciu ktoru som robil tato je uz mozno performna)

