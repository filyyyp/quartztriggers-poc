
Pri testovaní Java Spring Boot aplikácie s využitím Quartz bolo expirovaných **10 000 produktov** v intervale **300 sekúnd** bez závažných problémov alebo kritických oneskorení.

## Triggers v in-memory store

Expirácie prebehli plynule, žiadne oneskorenia:

- **Rýchle naplánovanie** a vykonávanie triggerov v pamäti.
- Expirácie boli vykonané presne v plánovanom čase bez meškania.
  ![triggers-in-memory.png](triggers-in-memory.png)
- Triggers in-memory: bez oneskorenia.

## Triggers v JDBC store

Expirácie prebehli v poriadku, no bol zaznamenaný úvodný oneskorený štart:

- **Oneskorenia na začiatku** súviseli s rýchlym generovaním veľkého počtu triggerov na expirácie, keďže ich naplánovanie cez databázu trvalo takmer minútu.
- Po úvodnej fáze sa však plánovanie stabilizovalo a expirácie prebiehali správne.
  ![triggers-in-jdbc.png](triggers-in-jdbc.png)
- Triggers v JDBC: úvodné oneskorenie, následne stabilná expirácia.


## Scheduled job s intervalom 1 minúta

Pravidelné oneskorenie je prirodzeným dôsledkom plánovania jobu každú minútu:

- **Expirácie sa vykonávajú každých 60 sekúnd**, čím môže byť pozorované zdržanie medzi expirovaním produktu a samotným vykonaním odstránenia.
- Skrátenie intervalu jobu by mohlo znížiť čas oneskorenia pri expiráciách.
  ![scheduled-job-1m.png](scheduled-job-1m.png)
- Scheduled job: pravidelné oneskorenie v dôsledku frekvencie vykonávania jobu.
