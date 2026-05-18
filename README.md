Versiones del proyecto
Java 17
Maven  3.9.11
Spring boot 3.5.14
Angular 19.2.26
Node.js 18.20.8

git clone https://github.com/AraiGonzalez/Desafio-FACTORIT.git

Backend
cd cart_service
Disponible en: http://localhost:8080

Frontend
cd ecommerce-frontend
npm install
ng serve
Disponible en: http://localhost:4200

Base de datos — H2
La base de datos H2 es en memoria, se crea automáticamente al levantar el proyecto. No requiere instalación.
Consola web: http://localhost:8080/h2-console
CampoValorJDBC URLjdbc: jdbc:h2:mem:ecommercedb
userName: root
password: root

Swagger
Documentación de los endpoints REST disponible en:
http://localhost:8080/swagger-ui.html

SOAP
WSDL disponible en: http://localhost:8080/ws/customers.wsdl
Para probar los endpoints SOAP en Postman (POST con body XML al endpoint http://localhost:8080/ws). 
