#  White Rabbit

Una herramienta de registro de tiempo.


##  Documentación

- User Guide  
- Troubleshooting  
- Changelog  
- Developer Guide  


##  Características

- Registra el inicio, el final y las interrupciones de tu jornada laboral.
- Almacena los datos en archivos JSON legibles por humanos (un archivo por mes).
- Permite hacer copia de seguridad creando un repositorio Git de la carpeta de datos y realizando un commit diario.


##  Tipos de día soportados

- `WORK` – Día laboral normal (lunes a viernes)
- `WEEKEND` – Fin de semana (detectado automáticamente)
- `HOLIDAY` – Día festivo (no descuenta horas extra)
- `VACATION` – Vacaciones (no descuenta horas extra)
- `FLEX_TIME` – Día flexible (descuenta horas extra)
- `SICK` – Baja por enfermedad (no descuenta horas extra)


##  Funcionamiento automático

Solo tienes que dejar la aplicación en ejecución y registrará tu jornada automáticamente.

###  Detecta el inicio cuando:
- Se inicia el programa
- El ordenador sale del modo suspensión por la mañana

###  Detecta el final cuando:
- Se cierra el programa
- El ordenador entra en suspensión el resto del día
- Pulsas el botón **"Stop working for today"**

###  Interrupciones
- Cuando el ordenador está en suspensión más de 2 minutos


##  Informes

- Genera informes de vacaciones
- Genera informes mensuales de tiempo trabajado


##  Seguridad

- Detecta si se ejecuta una segunda instancia para evitar corrupción de datos
