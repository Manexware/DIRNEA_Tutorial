# Construyendo servicios REST con Spring

REST se ha convertido rápidamente en el estándar de facto para crear servicios web en la web porque son fáciles de construir y de consumir.

Hay una discusión mucho más amplia sobre cómo REST encaja en el mundo de los microservicios, pero, para este tutorial, veamos cómo construir servicios RESTful.

¿Por qué descansar? REST adopta los preceptos de la web, incluida su arquitectura, beneficios y todo lo demás. Esto no es sorprendente dado que su autor, Roy Fielding, estuvo involucrado en probablemente una docena de especificaciones que rigen cómo funciona la web.

Que beneficios La web y su protocolo central, HTTP, proporcionan una pila de características:

- Acciones adecuadas ( `GET`, `POST`, `PUT`, `DELETE`, ...)
- Almacenamiento en caché
- Redirección y reenvío
- Seguridad (encriptación y autenticación)

Todos estos son factores críticos en la construcción de servicios resilientes. Pero eso no es todo. La web está construida con muchas especificaciones pequeñas, por lo tanto, ha podido evolucionar fácilmente, sin atascarse en las "guerras de estándares".

Los desarrolladores pueden recurrir a kits de herramientas de terceros que implementan estas diversas especificaciones y al instante tienen la tecnología del cliente y del servidor a su alcance.

Entonces, basándose en HTTP, las API REST proporcionan los medios para construir API flexibles que pueden:

- Soporta compatibilidad con versiones anteriores
- API evolucionables
- Servicios escalables
- Servicios asegurables
- Un espectro de servicios sin estado a estado

Lo importante es darse cuenta de que REST, aunque ubicuo, no es un estándar, *per se* , sino un enfoque, un estilo, un conjunto de *restricciones* en su arquitectura que puede ayudarlo a construir sistemas a escala web. En este tutorial usaremos el portafolio de Spring para construir un servicio RESTful mientras aprovechamos las características sin pila de REST.

## Empezando

Mientras trabajamos en este tutorial, usaremos [Spring Boot](https://spring.io/projects/spring-boot) . Vaya a [Spring Initializr](https://start.spring.io/) y seleccione lo siguiente:

- Web
- PSD
- H2
- Lombok

Luego elija "Generar proyecto". A `.zip`se descargará. Descomprimirlo. Dentro encontrará un proyecto simple basado en Maven que incluye un `pom.xml`archivo de compilación (NOTA: *puede* usar Gradle. Los ejemplos en este tutorial estarán basados en Maven).

Spring Boot puede funcionar con cualquier IDE. Puede usar Eclipse, IntelliJ IDEA, Netbeans, etc. [Spring Tool Suite](https://spring.io/tools/) es una distribución IDE de código abierto basada en Eclipse que proporciona un superconjunto de la distribución Java EE de Eclipse. Incluye características que hacen que trabajar con aplicaciones Spring sea aún más fácil. De ninguna manera es obligatorio. Pero considérelo si desea ese **empuje** adicional para sus pulsaciones de teclas. Aquí hay un video que muestra cómo comenzar con STS y Spring Boot. Esta es una introducción general para familiarizarlo con las herramientas.

Si elige IntelliJ IDEA como su IDE para este tutorial, debe instalar el complemento lombok. Para ver cómo instalamos complementos en IntelliJ IDEA, eche un vistazo a la [gestión de complementos](https://www.jetbrains.com/help/idea/managing-plugins.html) . Después de esto, debe asegurarse de que la casilla de verificación "Habilitar procesamiento de anotación" esté marcada en: Preferencias → Compilador → Procesadores de anotación, como se describe [https://stackoverflow.com/questions/14866765/building-with-lomboks-slf4j-and- intellij-cannot-find-symbol-log](https://stackoverflow.com/questions/14866765/building-with-lomboks-slf4j-and-intellij-cannot-find-symbol-log)

<iframe src="https://www.youtube.com/embed/p8AdyMlpmPk?rel=0" frameborder="0" allowfullscreen=""></iframe>

## La historia hasta ahora ...

Comencemos con lo más simple que podemos construir. De hecho, para hacerlo lo más simple posible, incluso podemos dejar de lado los conceptos de REST. (Más adelante, agregaremos REST para comprender la diferencia).

Nuestro ejemplo modela un servicio de nómina simple que administra a los empleados de una empresa. En pocas palabras, debe almacenar los objetos de los empleados en una base de datos H2 en memoria y acceder a ellos a través de JPA. Esto se envolverá con una capa Spring MVC para acceder de forma remota.

/ src  main/java/com.manexware.springframework.model/Employee.java

```java
package com.manexware.springframework.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
class Employee {

  private @Id @GeneratedValue Long id;
  private String name;
  private String role;

  Employee() {}

  Employee(String name, String role) {
    this.name = name;
    this.role = role;
  }
}
```

A pesar de ser pequeña, esta clase de Java contiene mucho:

- `@Data`es una anotación de Lombok para crear todos los getters, setters, `equals`, `hash`, y `toString`métodos, basados en los campos.
- `@Entity` es una anotación JPA para preparar este objeto para el almacenamiento en un almacén de datos basado en JPA.
- `id`, `name`y `role`son el atributo de nuestro objeto de dominio, el primero se marca con más anotaciones JPA para indicar que es la clave principal y que el proveedor JPA completa automáticamente.
- se crea un constructor personalizado cuando necesitamos crear una nueva instancia, pero aún no tenemos una identificación.

Con esta definición de objeto de dominio, ahora podemos recurrir a [Spring Data JPA](https://spring.io/guides/gs/accessing-data-jpa/) para manejar las tediosas interacciones de la base de datos. Los repositorios de Spring Data son interfaces con métodos que admiten la lectura, actualización, eliminación y creación de registros en un almacén de datos back-end. Algunos repositorios también admiten paginación y clasificación de datos, según corresponda. Spring Data sintetiza implementaciones basadas en convenciones encontradas en la denominación de los métodos en la interfaz.

|      | Existen múltiples implementaciones de repositorio además de JPA. Puede usar Spring Data MongoDB, Spring Data GemFire, Spring Data Cassandra, etc. Para este tutorial, nos quedaremos con JPA. |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

/src/main/java/com.manexware.springframework.repository/EmployeeRepository.java

```java
package com.manexware.springframework.repository;

import org.springframework.data.jpa.repository.JpaRepository;

interface EmployeeRepository extends JpaRepository<Employee, Long> {

}
```

Esta interfaz extiende los JPA de Spring Data `JpaRepository`, especificando el tipo de dominio como `Employee`y el tipo de identificación como `Long`. Esta interfaz, aunque vacía en la superficie, tiene un gran impacto dado que admite:

- Crear nuevas instancias
- Actualización de las existentes
- Borrado
- Encontrar (uno, todos, por propiedades simples o complejas)

La [solución de repositorio de](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories) Spring Data hace posible eludir los detalles del almacén de datos y, en cambio, resuelve la mayoría de los problemas utilizando la terminología específica del dominio.

Lo creas o no, ¡esto es suficiente para lanzar una aplicación! Una aplicación Spring Boot es, como mínimo, un `public static void main`punto de entrada y la `@SpringBootApplication`anotación. Esto le dice a Spring Boot que ayude, siempre que sea posible.

nonrest / src / main / java / nómina / PayrollApplication.java

```java
package payroll;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PayrollApplication {

  public static void main(String... args) {
    SpringApplication.run(PayrollApplication.class, args);
  }
}COPIAR
```

`@SpringBootApplication`es una metaanotación que incluye **escaneo de componentes** , **configuración automática** y **soporte de propiedades** . No profundizaremos en los detalles de Spring Boot en este tutorial, pero en esencia, activará un contenedor de servlets y servirá nuestro servicio.

Sin embargo, una aplicación sin datos no es muy interesante, así que precarguemosla. La clase siguiente se cargará automáticamente por Spring:

nonrest / src / main / java / nómina / LoadDatabase.java

```java
package payroll;

import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
class LoadDatabase {

  @Bean
  CommandLineRunner initDatabase(EmployeeRepository repository) {
    return args -> {
      log.info("Preloading " + repository.save(new Employee("Bilbo Baggins", "burglar")));
      log.info("Preloading " + repository.save(new Employee("Frodo Baggins", "thief")));
    };
  }
}COPIAR
```

¿Qué sucede cuando se carga?

- Spring Boot ejecutará TODOS los `CommandLineRunner`beans una vez que se cargue el contexto de la aplicación.
- Este corredor solicitará una copia del `EmployeeRepository`que acaba de crear.
- Al usarlo, creará dos entidades y las almacenará.
- `@Slf4j`es una anotación de Lombok a autocreate una red basada en SLF4J `LoggerFactory`como `log`, lo que nos permite registrar estos recién creados "empleados".

Haz clic derecho y **Ejecutar** `PayRollApplication` , y esto es lo que obtienes:

Fragmento de la salida de la consola que muestra la precarga de datos

```
...
2018-08-09 11: 36: 26.169 INFO 74611 --- [main] nómina.LoadDatabase: Preloading Employee (id = 1, name = Bilbo Baggins, role = robo)
2018-08-09 11: 36: 26.174 INFO 74611 --- [main] nómina.LoadDatabase: Preloading Employee (id = 2, name = Frodo Baggins, role = thief)
...
```

Este no es **todo el** registro, sino solo los bits clave de la precarga de datos. (De hecho, echa un vistazo a toda la consola. Es glorioso).

## HTTP es la plataforma

Para envolver su repositorio con una capa web, debe recurrir a Spring MVC. Gracias a Spring Boot, hay poca infraestructura para codificar. En cambio, podemos centrarnos en acciones:

nonrest / src / main / java / nómina / EmployeeController.java

```java
package payroll;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
class EmployeeController {

  private final EmployeeRepository repository;

  EmployeeController(EmployeeRepository repository) {
    this.repository = repository;
  }

  // Aggregate root

  @GetMapping("/employees")
  List<Employee> all() {
    return repository.findAll();
  }

  @PostMapping("/employees")
  Employee newEmployee(@RequestBody Employee newEmployee) {
    return repository.save(newEmployee);
  }

  // Single item

  @GetMapping("/employees/{id}")
  Employee one(@PathVariable Long id) {

    return repository.findById(id)
      .orElseThrow(() -> new EmployeeNotFoundException(id));
  }

  @PutMapping("/employees/{id}")
  Employee replaceEmployee(@RequestBody Employee newEmployee, @PathVariable Long id) {

    return repository.findById(id)
      .map(employee -> {
        employee.setName(newEmployee.getName());
        employee.setRole(newEmployee.getRole());
        return repository.save(employee);
      })
      .orElseGet(() -> {
        newEmployee.setId(id);
        return repository.save(newEmployee);
      });
  }

  @DeleteMapping("/employees/{id}")
  void deleteEmployee(@PathVariable Long id) {
    repository.deleteById(id);
  }
}COPIAR
```

- `@RestController` indica que los datos devueltos por cada método se escribirán directamente en el cuerpo de la respuesta en lugar de representar una plantilla.
- An `EmployeeRepository`es inyectado por el constructor en el controlador.
- Tenemos rutas para cada operación ( `@GetMapping`, `@PostMapping`, `@PutMapping`y `@DeleteMapping`, lo que corresponde a HTTP `GET`, `POST`, `PUT`, y `DELETE`llamadas). (NOTA: es útil leer cada método y comprender lo que hacen).
- `EmployeeNotFoundException` es una excepción utilizada para indicar cuándo se busca un empleado pero no se lo encuentra.

nonrest / src / main / java / nómina / EmployeeNotFoundException.java

```java
package payroll;

class EmployeeNotFoundException extends RuntimeException {

  EmployeeNotFoundException(Long id) {
    super("Could not find employee " + id);
  }
}COPIAR
```

Cuando `EmployeeNotFoundException`se lanza un, este tidbit adicional de la configuración Spring MVC se usa para representar un **HTTP 404** :

nonrest / src / main / java / nómina / EmployeeNotFoundAdvice.java

```java
package payroll;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
class EmployeeNotFoundAdvice {

  @ResponseBody
  @ExceptionHandler(EmployeeNotFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  String employeeNotFoundHandler(EmployeeNotFoundException ex) {
    return ex.getMessage();
  }
}COPIAR
```

- `@ResponseBody` indica que este consejo se presenta directamente en el cuerpo de respuesta.
- `@ExceptionHandler`configura el consejo para responder solo si `EmployeeNotFoundException`se arroja un.
- `@ResponseStatus`dice emitir un `HttpStatus.NOT_FOUND`, es decir, un **HTTP 404** .
- El cuerpo del consejo genera el contenido. En este caso, da el mensaje de la excepción.

Para iniciar la aplicación, haga clic derecho `public static void main`en `PayRollApplication`y seleccione **Ejecutar** desde su IDE o:

Spring Initializr usa envoltura maven, así que escriba esto:

```
$ ./mvnw clean spring-boot: ejecutar
```

Alternativamente, utilizando la versión de Maven instalada, escriba esto:

```
$ mvn clean spring-boot: ejecutar
```

Cuando se inicia la aplicación, podemos interrogarla de inmediato.

```
$ curl -v localhost: 8080 / empleados
```

Esto producirá:

```
* Intentando :: 1 ...
* Conjunto TCP_NODELAY
* Conectado al puerto localhost (:: 1) 8080 (# 0)
> GET / empleados HTTP / 1.1
> Anfitrión: localhost: 8080
> Usuario-Agente: curl / 7.54.0
> Aceptar: * / *
>
<HTTP / 1.1 200
<Tipo de contenido: application / json; charset = UTF-8
<Codificación de transferencia: fragmentada
<Fecha: jue, 09 ago 2018 17:58:00 GMT
<
* La conexión n. ° 0 para alojar el host local se dejó intacta
[{"id": 1, "name": "Bilbo Baggins", "role": "ladrón"}, {"id": 2, "name": "Frodo Baggins", "role": "ladrón"} ]
```

Aquí puede ver los datos precargados, en un formato compactado.

Si intenta consultar a un usuario que no existe ...

```
$ curl -v localhost: 8080 / empleados / 99
```

Tienes ...

```
* Intentando :: 1 ...
* Conjunto TCP_NODELAY
* Conectado al puerto localhost (:: 1) 8080 (# 0)
> GET / empleados / 99 HTTP / 1.1
> Anfitrión: localhost: 8080
> Usuario-Agente: curl / 7.54.0
> Aceptar: * / *
>
<HTTP / 1.1 404
<Content-Type: text / plain; charset = UTF-8
<Contenido-Longitud: 26
<Fecha: jue, 09 de agosto de 2018 18:00:56 GMT
<
* La conexión n. ° 0 para alojar el host local se dejó intacta
No se pudo encontrar el empleado 99
```

Este mensaje muestra muy bien un error **HTTP 404** con el mensaje personalizado **No se pudo encontrar al empleado 99** .

No es difícil mostrar las interacciones codificadas actualmente ...

```
$ curl -X POST localhost: 8080 / empleados -H 'Tipo de contenido: application / json' -d '{"name": "Samwise Gamgee", "role": "gardener"}'
```

Crea un nuevo `Employee`registro y luego nos envía el contenido:

```
{"id": 3, "name": "Samwise Gamgee", "role": "jardinero"}
```

Puedes alterar al usuario:

```
$ curl -X PUT localhost: 8080 / employee / 3 -H 'Tipo de contenido: application / json' -d '{"name": "Samwise Gamgee", "role": "ring bearer"}'
```

Actualizaciones del usuario:

```
{"id": 3, "name": "Samwise Gamgee", "role": "portador del anillo"}
```

|      | Dependiendo de cómo construya su servicio puede tener impactos significativos. En esta situación, **reemplazar** es una mejor descripción que **actualizar** . Por ejemplo, si NO se proporcionó el nombre, se anularía. |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

Y puedes eliminar ...

```
$ curl -X DELETE localhost: 8080 / empleados / 3
$ curl localhost: 8080 / empleados / 3
No se pudo encontrar el empleado 3
```

Todo esto está muy bien, pero ¿tenemos un servicio RESTful todavía? (Si no entendió la pista, la respuesta es no).

Lo que falta

## ¿Qué hace que algo sea RESTANTE?

Hasta ahora, tiene un servicio basado en la web que maneja las operaciones principales que involucran datos de los empleados. Pero eso no es suficiente para hacer las cosas "RESTful".

- Las URL bonitas como / employee / 3 no son REST.
- Simplemente usando `GET`, `POST`etc. no son REST.
- Tener todas las operaciones CRUD establecidas no es REST.

De hecho, lo que hemos construido hasta ahora se describe mejor como **RPC** ( **Llamada a procedimiento remoto** ). Eso es porque no hay forma de saber cómo interactuar con este servicio. Si publicaste esto hoy, también tendrías que escribir un documento o alojar un portal de desarrollador en algún lugar con todos los detalles.

Esta declaración de Roy Fielding puede dar más pistas sobre la diferencia entre **REST** y **RPC** :

```
Me frustra la cantidad de personas que llaman a cualquier interfaz basada en HTTP API REST. El ejemplo de hoy es la API REST de SocialSite. Eso es RPC. Grita RPC. Hay tanto acoplamiento en la pantalla que se le debe dar una calificación X.

¿Qué se debe hacer para dejar claro el estilo arquitectónico REST sobre la noción de que el hipertexto es una restricción? En otras palabras, si el motor del estado de la aplicación (y, por lo tanto, la API) no está siendo impulsado por el hipertexto, entonces no puede ser RESTful y no puede ser una API REST. Período. ¿Hay algún manual roto en algún lugar que deba repararse?
```

El efecto secundario de NO incluir hipermedia en nuestras representaciones es que los clientes DEBEN codificar los URI de código duro para navegar por la API. Esto lleva a la misma naturaleza frágil que precedió al auge del comercio electrónico en la web. Es una señal de que nuestra salida JSON necesita un poco de ayuda.

Presentamos [Spring HATEOAS](https://spring.io/projects/spring-hateoas) , un proyecto de Spring destinado a ayudarlo a escribir resultados impulsados por hipermedia. Para actualizar su servicio a RESTful, agregue esto a su compilación:

Agregar Spring HATEOAS a pom.xml

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-hateoas</artifactId>
</dependency>COPIAR
```

Esta pequeña biblioteca nos dará las construcciones para definir un servicio RESTful y luego renderizarlo en un formato aceptable para el consumo del cliente.

Un ingrediente crítico para cualquier servicio RESTful es agregar [enlaces](https://tools.ietf.org/html/rfc5988) a operaciones relevantes. Para hacer que su controlador sea más RESTful, agregue enlaces como este:

Obtener un recurso de un solo elemento

```java
@GetMapping("/employees/{id}")
Resource<Employee> one(@PathVariable Long id) {

  Employee employee = repository.findById(id)
    .orElseThrow(() -> new EmployeeNotFoundException(id));

  return new Resource<>(employee,
    linkTo(methodOn(EmployeeController.class).one(id)).withSelfRel(),
    linkTo(methodOn(EmployeeController.class).all()).withRel("employees"));
}COPIAR
```

Declaraciones de importación relevantes

```java
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;COPIAR
```

Esto es muy similar a lo que teníamos antes, pero algunas cosas han cambiado:

- El tipo de retorno del método ha cambiado de `Employee`a `Resource<Employee>`. `Resource<T>`es un contenedor genérico de Spring HATEOAS que incluye no solo los datos sino también una colección de enlaces.
- `linkTo(methodOn(EmployeeController.class).one(id)).withSelfRel()`pide que Spring HATEOAS cree un enlace al método de `EmployeeController`'s `one()`y lo marque como un enlace [propio](https://www.iana.org/assignments/link-relations/link-relations.xhtml) .
- `linkTo(methodOn(EmployeeController.class).all()).withRel("employees")`le pide a Spring HATEOAS que cree un enlace a la raíz agregada `all()`y lo llame "empleados".

¿Qué queremos decir con "construir un enlace"? Uno de los tipos principales de Spring HATEOAS es `Link`. Incluye un **URI** y un **rel** (relación). Los enlaces son los que potencian la web. Antes de la World Wide Web, otros sistemas de documentos mostraban información o enlaces, pero era la vinculación de documentos CON datos lo que unía la web.

Roy Fielding fomenta la creación de API con las mismas técnicas que hicieron que la web sea exitosa, y los enlaces son una de ellas.

Si reinicia la aplicación y consulta el registro de empleados de **Bilbo** , obtendrá una respuesta ligeramente diferente a la anterior:

Representación RESTful de un solo empleado

```javascript
{
  "id": 1,
  "name": "Bilbo Baggins",
  "role": "burglar",
  "_links": {
    "self": {
      "href": "http://localhost:8080/employees/1"
    },
    "employees": {
      "href": "http://localhost:8080/employees"
    }
  }
}COPIAR
```

Esta salida descomprimida muestra no solo los elementos de datos que vio anteriormente ( `id`, `name`y `role`), sino también una `_links`entrada que contiene dos URI. Todo este documento está formateado con [HAL](http://stateless.co/hal_specification.html) .

HAL es un peso ligero [MediaType](https://tools.ietf.org/html/draft-kelly-json-hal-08) que permite la codificación no sólo datos, sino también controles hipermedia, alertando a los consumidores a otras partes de la API que pueden desplazarse hacia. En este caso, hay un enlace "propio" (como una `this`declaración en el código) junto con un enlace a la **raíz agregada** .

Para hacer que la raíz agregada TAMBIÉN sea más RESTful, desea incluir enlaces de nivel superior y TAMBIÉN incluir componentes RESTful dentro de:

Obtener un recurso raíz agregado

```java
@GetMapping("/employees")
Resources<Resource<Employee>> all() {

  List<Resource<Employee>> employees = repository.findAll().stream()
    .map(employee -> new Resource<>(employee,
      linkTo(methodOn(EmployeeController.class).one(employee.getId())).withSelfRel(),
      linkTo(methodOn(EmployeeController.class).all()).withRel("employees")))
    .collect(Collectors.toList());

  return new Resources<>(employees,
    linkTo(methodOn(EmployeeController.class).all()).withSelfRel());
}COPIAR
```

¡Guauu! ¡Ese método, que solía ser, `repository.findAll()`ha crecido mucho! Vamos a desempacarlo.

`Resources<>`es otro contenedor Spring HATEOAS destinado a encapsular colecciones. También, también te permite incluir enlaces. No dejes pasar esa primera declaración. ¿Cuándo significa "encapsular colecciones"? Colecciones de empleados?

No exactamente.

Como estamos hablando de REST, debería encapsular colecciones de **recursos** de **empleados** .

Es por eso que busca a todos los empleados, pero luego los transforma en una lista de `Resource<Employee>`objetos. (¡Gracias Java 8 Stream API!)

Si reinicia la aplicación y busca la raíz agregada, puede ver cómo se ve.

RESTful representación de una colección de recursos de empleados

```javascript
{
  "_embedded": {
    "employeeList": [
      {
        "id": 1,
        "name": "Bilbo Baggins",
        "role": "burglar",
        "_links": {
          "self": {
            "href": "http://localhost:8080/employees/1"
          },
          "employees": {
            "href": "http://localhost:8080/employees"
          }
        }
      },
      {
        "id": 2,
        "name": "Frodo Baggins",
        "role": "thief",
        "_links": {
          "self": {
            "href": "http://localhost:8080/employees/2"
          },
          "employees": {
            "href": "http://localhost:8080/employees"
          }
        }
      }
    ]
  },
  "_links": {
    "self": {
      "href": "http://localhost:8080/employees"
    }
  }
}COPIAR
```

Para esta raíz agregada, que sirve una colección de recursos de empleados, hay un enlace **"propio" de** nivel superior . La **"colección"** aparece debajo de la sección **"_embedded"** . Así es como HAL representa colecciones.

Y cada miembro individual de la colección tiene su información y enlaces relacionados.

¿Cuál es el punto de agregar todos estos enlaces? Permite evolucionar los servicios REST con el tiempo. Los enlaces existentes se pueden mantener mientras se agregan nuevos enlaces en el futuro. Los clientes más nuevos pueden aprovechar los nuevos enlaces, mientras que los clientes heredados pueden mantenerse en los enlaces antiguos. Esto es especialmente útil si los servicios se reubican y se trasladan. Mientras se mantenga la estructura de enlaces, los clientes TODAVÍA pueden encontrar e interactuar con las cosas.

## Simplificando la creación de enlaces

¿Notó la repetición en la creación de enlaces de un solo empleado? El código para proporcionar un enlace único a un empleado, así como un enlace "empleados" a la raíz agregada se mostró dos veces. Si eso te preocupa, ¡bien! Hay una solución

En pocas palabras, debe definir una función que convierta `Employee`objetos en `Resource<Employee>`objetos. Si bien puede codificar fácilmente este método usted mismo, hay beneficios en el futuro de la implementación de la `ResourceAssembler`interfaz de Spring HATEOAS .

evolution / src / main / java / nómina / EmployeeResourceAssembler.java

```java
package payroll;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

@Component
class EmployeeResourceAssembler implements ResourceAssembler<Employee, Resource<Employee>> {

  @Override
  public Resource<Employee> toResource(Employee employee) {

    return new Resource<>(employee,
      linkTo(methodOn(EmployeeController.class).one(employee.getId())).withSelfRel(),
      linkTo(methodOn(EmployeeController.class).all()).withRel("employees"));
  }
}COPIAR
```

Esta interfaz simple tiene un método: `toResource()`. Se basa en convertir un objeto que no sea de recursos ( `Employee`) en un objeto basado en recursos ( `Resource<Employee>`).

Todo el código que vio anteriormente en el controlador se puede mover a esta clase. Y al aplicar Spring Framework `@Component`, este componente se creará automáticamente cuando se inicie la aplicación.

|      | La clase base abstracta de Spring HATEOAS para todos los recursos es `ResourceSupport`. Pero por simplicidad, recomiendo usar `Resource<T>`como mecanismo para envolver fácilmente todos los POJO como recursos. |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

Para aprovechar este ensamblador, solo tiene que modificarlo `EmployeeController`inyectando el ensamblador en el constructor. Entonces el

Inyectando EmployeeResourceAssembler en el controlador

```java
@RestController
class EmployeeController {

  private final EmployeeRepository repository;

  private final EmployeeResourceAssembler assembler;

  EmployeeController(EmployeeRepository repository,
             EmployeeResourceAssembler assembler) {

    this.repository = repository;
    this.assembler = assembler;
  }

  ...

}COPIAR
```

Desde aquí, puede usarlo en el método de empleado de un solo elemento:

Obteniendo un recurso de un solo elemento usando el ensamblador

```java
@GetMapping("/employees/{id}")
Resource<Employee> one(@PathVariable Long id) {

  Employee employee = repository.findById(id)
    .orElseThrow(() -> new EmployeeNotFoundException(id));

  return assembler.toResource(employee);
}COPIAR
```

Este código es casi el mismo, excepto que en lugar de crear la `Resource<Employee>`instancia aquí, lo delega al ensamblador. Tal vez eso no se parece mucho?

Aplicar lo mismo en el método de controlador raíz agregado es más impresionante:

Obteniendo recurso raíz agregado usando el ensamblador

```java
@GetMapping("/employees")
Resources<Resource<Employee>> all() {

  List<Resource<Employee>> employees = repository.findAll().stream()
    .map(assembler::toResource)
    .collect(Collectors.toList());

  return new Resources<>(employees,
    linkTo(methodOn(EmployeeController.class).all()).withSelfRel());
}COPIAR
```

El código es, nuevamente, casi el mismo, sin embargo, puedes reemplazar toda esa `Resource<Employee>`lógica de creación `map(assembler::toResource)`. Gracias a las referencias de métodos de Java 8, es muy fácil enchufarlo y simplificar su controlador.

|      | Un objetivo de diseño clave de Spring HATEOAS es hacer que sea más fácil hacer The Right Thing ™. En este escenario, agregar hipermedia a su servicio sin codificar nada. |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

¡En esta etapa, ha creado un controlador Spring MVC REST que realmente produce contenido hipermedia! Los clientes que no hablan HAL pueden ignorar los bits adicionales mientras consumen los datos puros. Los clientes que hablan HAL pueden navegar por su API habilitada.

Pero eso no es lo único necesario para construir un servicio verdaderamente RESTful con Spring.

## API REST en evolución

Con una biblioteca adicional y algunas líneas de código extra, ha agregado hipermedia a su aplicación. Pero eso no es lo único necesario para que su servicio sea RESTful. Una faceta importante de REST es el hecho de que no es una pila tecnológica ni un estándar único.

REST es una colección de restricciones arquitectónicas que, cuando se adoptan, hacen que su aplicación sea mucho más resistente. Un factor clave de resistencia es que cuando realiza actualizaciones a sus servicios, sus clientes no sufren tiempos de inactividad.

En los "viejos" días, las actualizaciones eran notorias por romper clientes. En otras palabras, una actualización del servidor requería una actualización del cliente. En la actualidad, las horas o incluso los minutos de tiempo de inactividad dedicados a una actualización pueden costar millones de dólares en ingresos perdidos.

Algunas compañías requieren que presente a la gerencia un plan para minimizar el tiempo de inactividad. En el pasado, podía salirse con la actualización a las 2:00 a.m. de un domingo cuando la carga era mínima. Pero en el comercio electrónico actual basado en Internet con clientes internacionales, tales estrategias no son tan efectivas.

Los servicios basados en SOAP y los servicios basados en CORBA fueron increíblemente frágiles. Fue difícil implementar un servidor que pudiera admitir clientes antiguos y nuevos. Con las prácticas basadas en REST, es mucho más fácil. Especialmente usando la pila Spring.

Imagine este problema de diseño: ha implementado un sistema con este `Employee`registro basado. El sistema es un gran éxito. Has vendido tu sistema a innumerables empresas. De repente, la necesidad de que el nombre de un empleado que se divide en `firstName`y `lastName`surge.

UH oh. No pensé en eso.

Antes de abrir la `Employee`clase y reemplazar el campo individual `name`con `firstName`y `lastName`, deténgase y piense por un segundo. ¿Eso romperá a algún cliente? ¿Cuánto tiempo llevará actualizarlos? ¿Incluso controlas a todos los clientes que acceden a tus servicios?

Tiempo de inactividad = dinero perdido. ¿La gerencia está lista para eso?

Hay una vieja estrategia que precede a REST por años.

> Nunca elimine una columna en una base de datos.

\- Desconocido

Siempre puede agregar columnas (campos) a una tabla de base de datos. Pero no te lleves uno. El principio en los servicios RESTful es el mismo. Agregue nuevos campos a sus representaciones JSON, pero no elimine ninguno. Me gusta esto:

JSON que admite múltiples clientes

```javascript
{
  "id": 1,
  "firstName": "Bilbo",
  "lastName": "Baggins",
  "role": "burglar",
  "name": "Bilbo Baggins",
  "_links": {
    "self": {
      "href": "http://localhost:8080/employees/1"
    },
    "employees": {
      "href": "http://localhost:8080/employees"
    }
  }
}COPIAR
```

Observe cómo este formato muestra `firstName`, `lastName`Y `name`? Si bien tiene una duplicación de información, el propósito es apoyar a los clientes antiguos y nuevos. Eso significa que puede actualizar el servidor sin requerir que los clientes actualicen al mismo tiempo. Un buen movimiento que debería reducir el tiempo de inactividad.

Y no solo debe mostrar esta información tanto en la "forma anterior" como en la "nueva", sino que también debe procesar los datos entrantes en ambos sentidos.

¿Cómo? Simple. Me gusta esto:

Registro de empleados que maneja clientes "viejos" y "nuevos"

```java
package payroll;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@Entity
class Employee {

  private @Id @GeneratedValue Long id;
  private String firstName;
  private String lastName;
  private String role;

  Employee() {}

  Employee(String firstName, String lastName, String role) {
    this.firstName = firstName;
    this.lastName = lastName;
    this.role = role;
  }

  public String getName() {
    return this.firstName + " " + this.lastName;
  }

  public void setName(String name) {
    String[] parts =name.split(" ");
    this.firstName = parts[0];
    this.lastName = parts[1];
  }
}COPIAR
```

Esta clase es muy similar a la versión anterior de `Employee`. Repasemos los cambios:

- El campo `name`ha sido reemplazado por `firstName`y `lastName`. Lombok generará getters y setters para esos.
- Se define un captador "virtual" para la `name`propiedad anterior `getName()`. Utiliza los campos `firstName`y `lastName`para producir un valor.
- Un colocador "virtual" de la antigua `name`propiedad también se define, `setName()`. Analiza una cadena entrante y la almacena en los campos adecuados.

Por supuesto, CADA cambio a su API es tan simple como dividir una cadena o fusionar dos cadenas. Pero seguramente no es imposible llegar a un conjunto de transformaciones para la mayoría de los escenarios, ¿eh?

Otro ajuste fino es garantizar que cada uno de sus métodos REST devuelva una respuesta adecuada. Actualice el método POST de esta manera:

POST que maneja solicitudes de clientes "antiguos" y "nuevos"

```java
@PostMapping("/employees")
ResponseEntity<?> newEmployee(@RequestBody Employee newEmployee) throws URISyntaxException {

  Resource<Employee> resource = assembler.toResource(repository.save(newEmployee));

  return ResponseEntity
    .created(new URI(resource.getId().expand().getHref()))
    .body(resource);
}COPIAR
```

- El nuevo `Employee`objeto se guarda como antes. Pero el objeto resultante se envuelve usando el `EmployeeResourceAssembler`.
- Spring MVC `ResponseEntity`se usa para crear un mensaje de estado **HTTP 201 Creado** . Este tipo de respuesta generalmente incluye un encabezado de respuesta de **ubicación** , y usamos el enlace recién formado.
- Además, devuelva la versión basada en recursos del objeto guardado.

Con este ajuste, puede usar el mismo punto final para crear un nuevo recurso de empleado y usar el `name`campo heredado :

```
$ curl -v -X POST localhost: 8080 / empleados -H 'Tipo de contenido: application / json' -d '{"name": "Samwise Gamgee", "role": "gardener"}'
```

La salida se muestra a continuación:

```
> POST / empleados HTTP / 1.1
> Anfitrión: localhost: 8080
> Usuario-Agente: curl / 7.54.0
> Aceptar: * / *
> Tipo de contenido: application / json
> Contenido-Longitud: 46
>
<Ubicación: http: // localhost: 8080 / employee / 3
<Tipo de contenido: application / hal + json; charset = UTF-8
<Codificación de transferencia: fragmentada
<Fecha: viernes, 10 de agosto de 2018 19:44:43 GMT
<
{
  "id": 3,
  "firstName": "Samwise",
  "apellido": "Gamgee",
  "role": "jardinero",
  "nombre": "Samwise Gamgee",
  "_Enlaces": {
    "self": {
      "href": "http: // localhost: 8080 / employee / 3"
    },
    "empleados": {
      "href": "http: // localhost: 8080 / empleados"
    }
  }
}
```

Esto no solo tiene el objeto resultante representado en HAL (tanto `name`como `firstName`/ `lastName`), sino también el encabezado de **ubicación** rellenado `http://localhost:8080/employees/3`. Un cliente con hipermedia podría optar por "navegar" a este nuevo recurso y proceder a interactuar con él.

El método del controlador PUT necesita ajustes similares:

Manejo de un PUT para diferentes clientes

```java
@PutMapping("/employees/{id}")
ResponseEntity<?> replaceEmployee(@RequestBody Employee newEmployee, @PathVariable Long id) throws URISyntaxException {

  Employee updatedEmployee = repository.findById(id)
    .map(employee -> {
      employee.setName(newEmployee.getName());
      employee.setRole(newEmployee.getRole());
      return repository.save(employee);
    })
    .orElseGet(() -> {
      newEmployee.setId(id);
      return repository.save(newEmployee);
    });

  Resource<Employee> resource = assembler.toResource(updatedEmployee);

  return ResponseEntity
    .created(new URI(resource.getId().expand().getHref()))
    .body(resource);
}COPIAR
```

El `Employee`objeto construido a partir de la `save()`operación se envuelve usando `EmployeeResourceAssembler`un `Resource<Employee>`objeto. Como queremos un código de respuesta HTTP más detallado que **200 OK** , usaremos el `ResponseEntity`contenedor Spring MVC . Tiene un método estático útil `created()`donde podemos conectar el URI del recurso.

Al agarrar el `resource`puede obtener su enlace "self" a través de la `getId()`llamada al método. Este método produce un `Link`que puede convertir en Java `URI`. Para atar bien las cosas, se inyecta `resource`el `body()`método en sí mismo .

|      | En REST, la **identificación de** un recurso es el URI de ese recurso. Por lo tanto, Spring HATEOAS no le entrega el `id`campo del tipo de datos subyacente (que ningún cliente debería), sino el URI correspondiente. Y no confundir `ResourceSupport.getId()`con `Employee.getId()`. |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

Es discutible si **HTTP 201 Created** lleva la semántica correcta ya que no estamos necesariamente "creando" un nuevo recurso. Pero viene precargado con un encabezado de respuesta de **ubicación** , así que ejecútelo.

```
$ curl -v -X PUT localhost: 8080 / employee / 3 -H 'Content-Type: application / json' -d '{"name": "Samwise Gamgee", "role": "ring bearer"}'

* Conjunto TCP_NODELAY
* Conectado al puerto localhost (:: 1) 8080 (# 0)
> PUT / empleados / 3 HTTP / 1.1
> Anfitrión: localhost: 8080
> Usuario-Agente: curl / 7.54.0
> Aceptar: * / *
> Tipo de contenido: application / json
> Contenido-Longitud: 49
>
<HTTP / 1.1 201
<Ubicación: http: // localhost: 8080 / employee / 3
<Tipo de contenido: application / hal + json; charset = UTF-8
<Codificación de transferencia: fragmentada
<Fecha: viernes, 10 de agosto de 2018 19:52:56 GMT
{
	"id": 3,
	"firstName": "Samwise",
	"apellido": "Gamgee",
	"role": "portador del anillo",
	"nombre": "Samwise Gamgee",
	"_Enlaces": {
		"self": {
			"href": "http: // localhost: 8080 / employee / 3"
		},
		"empleados": {
			"href": "http: // localhost: 8080 / empleados"
		}
	}
}
```

Ese recurso de empleado ahora se ha actualizado y se ha devuelto el URI de ubicación. Finalmente, actualice la operación DELETE adecuadamente:

Manejo de solicitudes DELETE

```java
@DeleteMapping("/employees/{id}")
ResponseEntity<?> deleteEmployee(@PathVariable Long id) {

  repository.deleteById(id);

  return ResponseEntity.noContent().build();
}COPIAR
```

Esto devuelve una respuesta **HTTP 204 Sin contenido** .

```
$ curl -v -X BORRAR localhost: 8080 / empleados / 1

* Conjunto TCP_NODELAY
* Conectado al puerto localhost (:: 1) 8080 (# 0)
> BORRAR / empleados / 1 HTTP / 1.1
> Anfitrión: localhost: 8080
> Usuario-Agente: curl / 7.54.0
> Aceptar: * / *
>
<HTTP / 1.1 204
<Fecha: viernes, 10 de agosto de 2018 21:30:26 GMT
```

|      | Realizar cambios en los campos de la `Employee`clase requerirá coordinación con el equipo de su base de datos, para que puedan migrar correctamente el contenido existente a las nuevas columnas. |
| ---- | ------------------------------------------------------------ |
|      |                                                              |

¡Ahora está listo para una actualización que NO molestará a los clientes existentes, mientras que los clientes más nuevos pueden aprovechar las mejoras!

Por cierto, ¿te preocupa enviar demasiada información por cable? En algunos sistemas donde cada byte cuenta, la evolución de las API puede necesitar pasar a un segundo plano. Pero no persiga una optimización tan prematura hasta que mida.

## Construyendo enlaces en su API REST

Hasta ahora, ha creado una API evolutiva con enlaces básicos. Para hacer crecer su API y servir mejor a sus clientes, debe adoptar el concepto de **Hypermedia como el motor del estado de la aplicación** .

Qué significa eso? En esta sección, lo explorarás en detalle.

La lógica empresarial inevitablemente construye reglas que involucran procesos. El riesgo de tales sistemas es que a menudo llevamos esa lógica del lado del servidor a los clientes y desarrollamos un fuerte acoplamiento. REST se trata de romper tales conexiones y minimizar dicho acoplamiento.

Para mostrar cómo hacer frente a los cambios de estado sin desencadenar cambios importantes en los clientes, imagine agregar un sistema que cumpla los pedidos.

Como primer paso, defina un `Order`registro:

enlaces / src / main / java / nómina / Order.java

```java
package payroll;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Data
@Table(name = "CUSTOMER_ORDER")
class Order {

  private @Id @GeneratedValue Long id;

  private String description;
  private Status status;

  Order() {}

  Order(String description, Status status) {

    this.description = description;
    this.status = status;
  }
}COPIAR
```

- La clase requiere una `@Table`anotación JPA que cambia el nombre de la tabla a `CUSTOMER_ORDER`porque `ORDER`no es un nombre válido para la tabla.
- Incluye tanto un `description`campo como un `status`campo.

Los pedidos deben pasar por una cierta serie de transiciones de estado desde el momento en que un cliente envía un pedido y éste se completa o cancela. Esto se puede capturar como Java `enum`:

enlaces / src / main / java / nómina / Status.java

```java
package payroll;

enum Status {

  IN_PROGRESS,
  COMPLETED,
  CANCELLED;
}COPIAR
```

Esto `enum`captura los diversos estados que `Order`puede ocupar. Para este tutorial, hagámoslo simple.

Para admitir la interacción con los pedidos en la base de datos, debe definir un repositorio Spring Data correspondiente:

`JpaRepository`Interfaz base de Spring Data JPA

```java
interface OrderRepository extends JpaRepository<Order, Long> {
}COPIAR
```

Con esto en su lugar, ahora puede definir un básico `OrderController`:

enlaces / src / main / java / nómina / OrderController.java

```java
@RestController
class OrderController {

  private final OrderRepository orderRepository;
  private final OrderResourceAssembler assembler;

  OrderController(OrderRepository orderRepository,
          OrderResourceAssembler assembler) {

    this.orderRepository = orderRepository;
    this.assembler = assembler;
  }

  @GetMapping("/orders")
  Resources<Resource<Order>> all() {

    List<Resource<Order>> orders = orderRepository.findAll().stream()
      .map(assembler::toResource)
      .collect(Collectors.toList());

    return new Resources<>(orders,
      linkTo(methodOn(OrderController.class).all()).withSelfRel());
  }

  @GetMapping("/orders/{id}")
  Resource<Order> one(@PathVariable Long id) {
    return assembler.toResource(
      orderRepository.findById(id)
        .orElseThrow(() -> new OrderNotFoundException(id)));
  }

  @PostMapping("/orders")
  ResponseEntity<Resource<Order>> newOrder(@RequestBody Order order) {

    order.setStatus(Status.IN_PROGRESS);
    Order newOrder = orderRepository.save(order);

    return ResponseEntity
      .created(linkTo(methodOn(OrderController.class).one(newOrder.getId())).toUri())
      .body(assembler.toResource(newOrder));
  }
}COPIAR
```

- Contiene la misma configuración de controlador REST que los controladores que ha construido hasta ahora.
- Inyecta tanto un `OrderRepository`como un (aún no construido) `OrderResourceAssembler`.
- Las dos primeras rutas Spring MVC manejan la raíz agregada, así como una `Order`solicitud de recurso de un solo elemento .
- La tercera ruta Spring MVC maneja la creación de nuevos pedidos, al iniciarlos en el `IN_PROGRESS`estado.
- Todos los métodos de controlador devuelven una de las `ResourceSupport`subclases de Spring HATEOAS para representar adecuadamente hipermedia (o un contenedor alrededor de ese tipo).

Antes de construir el `OrderResourceAssembler`, discutamos lo que debe suceder. Usted está modelando el flujo de estados entre `Status.IN_PROGRESS`, `Status.COMPLETED`y `Status.CANCELLED`. Una cosa natural al entregar tales datos a los clientes es dejar que los clientes tomen una decisión sobre lo que pueden hacer en función de esta carga útil.

Pero eso estaria mal.

¿Qué sucede cuando introduce un nuevo estado en este flujo? La colocación de varios botones en la interfaz de usuario probablemente sería errónea.

¿Qué sucede si cambiaste el nombre de cada estado, tal vez mientras codificabas el soporte internacional y mostrabas texto específico de la localidad para cada estado? Eso probablemente rompería a todos los clientes.

Ingrese **HATEOAS** o **Hypermedia como el motor del estado de la aplicación** . En lugar de que los clientes analicen la carga útil, bríndeles enlaces para señalar acciones válidas. Desacoplar acciones basadas en estado de la carga útil de datos. En otras palabras, cuando **CANCELAR** y **COMPLETAR** son acciones válidas, agréguelas dinámicamente a la lista de enlaces. Los clientes solo necesitan mostrar a los usuarios los botones correspondientes cuando existen los enlaces.

Esto separa a los clientes de tener que saber CUANDO tales acciones son válidas, reduciendo el riesgo de que el servidor y sus clientes se desincronicen en la lógica de las transiciones de estado.

Habiendo adoptado el concepto de `ResourceAssembler`componentes Spring HATEOAS , poner esa lógica en el `OrderResourceAssembler`sería el lugar perfecto para capturar esta regla de negocio:

enlaces / src / main / java / nómina / OrderResourceAssembler.java

```java
package payroll;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.*;

import org.springframework.hateoas.Resource;
import org.springframework.hateoas.ResourceAssembler;
import org.springframework.stereotype.Component;

@Component
class OrderResourceAssembler implements ResourceAssembler<Order, Resource<Order>> {

  @Override
  public Resource<Order> toResource(Order order) {

    // Unconditional links to single-item resource and aggregate root

    Resource<Order> orderResource = new Resource<>(order,
      linkTo(methodOn(OrderController.class).one(order.getId())).withSelfRel(),
      linkTo(methodOn(OrderController.class).all()).withRel("orders")
    );

    // Conditional links based on state of the order

    if (order.getStatus() == Status.IN_PROGRESS) {
      orderResource.add(
        linkTo(methodOn(OrderController.class)
          .cancel(order.getId())).withRel("cancel"));
      orderResource.add(
        linkTo(methodOn(OrderController.class)
          .complete(order.getId())).withRel("complete"));
    }

    return orderResource;
  }
}COPIAR
```

Este ensamblador de recursos siempre incluye el enlace **propio** al recurso de un solo elemento, así como un enlace de regreso a la raíz agregada. Pero también incluye dos enlaces condicionales a `OrderController.cancel(id)`, así como `OrderController.complete(id)`(aún no definido). Estos enlaces SOLO se muestran cuando el estado del pedido es `Status.IN_PROGRESS`.

Si los clientes pueden adoptar HAL y la capacidad de leer enlaces en lugar de simplemente leer los datos de JSON antiguo, pueden intercambiar la necesidad de conocimiento del dominio sobre el sistema de pedidos. Esto, naturalmente, reduce el acoplamiento entre el cliente y el servidor. Y abre la puerta para ajustar el flujo de cumplimiento de pedidos sin interrumpir a los clientes en el proceso.

Para redondear el cumplimiento del pedido, agregue lo siguiente `OrderController`para la `cancel`operación:

Crear una operación "cancelar" en el OrderController

```java
@DeleteMapping("/orders/{id}/cancel")
ResponseEntity<ResourceSupport> cancel(@PathVariable Long id) {

  Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

  if (order.getStatus() == Status.IN_PROGRESS) {
    order.setStatus(Status.CANCELLED);
    return ResponseEntity.ok(assembler.toResource(orderRepository.save(order)));
  }

  return ResponseEntity
    .status(HttpStatus.METHOD_NOT_ALLOWED)
    .body(new VndErrors.VndError("Method not allowed", "You can't cancel an order that is in the " + order.getStatus() + " status"));
}COPIAR
```

Comprueba el `Order`estado antes de permitir que se cancele. Si no es un estado válido, devuelve un Spring HATEOAS `VndError`, un contenedor de errores compatible con hipermedia. Si la transición es realmente válida, hace la transición `Order`a `CANCELLED`.

Y agregue esto al `OrderController`también para completar el pedido:

Crear una operación "completa" en OrderController

```java
@PutMapping("/orders/{id}/complete")
ResponseEntity<ResourceSupport> complete(@PathVariable Long id) {

    Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));

    if (order.getStatus() == Status.IN_PROGRESS) {
      order.setStatus(Status.COMPLETED);
      return ResponseEntity.ok(assembler.toResource(orderRepository.save(order)));
    }

    return ResponseEntity
      .status(HttpStatus.METHOD_NOT_ALLOWED)
      .body(new VndErrors.VndError("Method not allowed", "You can't complete an order that is in the " + order.getStatus() + " status"));
}COPIAR
```

Esto implementa una lógica similar para evitar que `Order`se complete un estado a menos que se encuentre en el estado correcto.

Al agregar un pequeño código de inicialización adicional a `LoadDatabase`:

Actualización del precargador de bases de datos

```java
orderRepository.save(new Order("MacBook Pro", Status.COMPLETED));
orderRepository.save(new Order("iPhone", Status.IN_PROGRESS));

orderRepository.findAll().forEach(order -> {
  log.info("Preloaded " + order);
});COPIAR
```

... puedes probar cosas!

Para usar el servicio de pedido recién creado, solo realice algunas operaciones:

```
$ curl -v http: // localhost: 8080 / orders

{
  "_incrustado": {
    "lista de orden": [
      {
        "id": 3,
        "descripción": "MacBook Pro",
        "Estado: COMPLETADO",
        "_Enlaces": {
          "self": {
            "href": "http: // localhost: 8080 / orders / 3"
          },
          "pedidos": {
            "href": "http: // localhost: 8080 / orders"
          }
        }
      },
      {
        "id": 4,
        "descripción": "iPhone",
        "estado en progreso",
        "_Enlaces": {
          "self": {
            "href": "http: // localhost: 8080 / orders / 4"
          },
          "pedidos": {
            "href": "http: // localhost: 8080 / orders"
          },
          "cancelar": {
            "href": "http: // localhost: 8080 / orders / 4 / cancel"
          },
          "completo": {
            "href": "http: // localhost: 8080 / orders / 4 / complete"
          }
        }
      }
    ]
  },
  "_Enlaces": {
    "self": {
      "href": "http: // localhost: 8080 / orders"
    }
  }
}
```

Este documento HAL muestra inmediatamente diferentes enlaces para cada pedido, en función de su estado actual.

- El primer orden, **COMPLETADO,** solo tiene los enlaces de navegación. Los enlaces de transición de estado no se muestran.
- El segundo pedido, que es **IN_PROGRESS,** además tiene el enlace **cancelar** , así como el enlace **completo** .

Intenta cancelar un pedido:

```
$ curl -v -X DELETE http: // localhost: 8080 / orders / 4 / cancel

> BORRAR / pedidos / 4 / cancelar HTTP / 1.1
> Anfitrión: localhost: 8080
> Usuario-Agente: curl / 7.54.0
> Aceptar: * / *
>
<HTTP / 1.1 200
<Tipo de contenido: application / hal + json; charset = UTF-8
<Codificación de transferencia: fragmentada
<Fecha: lunes, 27 de agosto de 2018 15:02:10 GMT
<
{
  "id": 4,
  "descripción": "iPhone",
  "estado": "CANCELADO",
  "_Enlaces": {
    "self": {
      "href": "http: // localhost: 8080 / orders / 4"
    },
    "pedidos": {
      "href": "http: // localhost: 8080 / orders"
    }
  }
}
```

Esta respuesta muestra un código de estado **HTTP 200** que indica que fue exitoso. El documento HAL de respuesta muestra ese orden en su nuevo estado ( `CANCELLED`). Y los enlaces que alteran el estado se han ido.

Si vuelve a intentar la misma operación ...

```
$ curl -v -X DELETE http: // localhost: 8080 / orders / 4 / cancel

* Conjunto TCP_NODELAY
* Conectado al puerto localhost (:: 1) 8080 (# 0)
> BORRAR / pedidos / 4 / cancelar HTTP / 1.1
> Anfitrión: localhost: 8080
> Usuario-Agente: curl / 7.54.0
> Aceptar: * / *
>
<HTTP / 1.1 405
<Tipo de contenido: application / hal + json; charset = UTF-8
<Codificación de transferencia: fragmentada
<Fecha: lunes, 27 de agosto de 2018 15:03:24 GMT
<
{
  "logref": "Método no permitido",
  "mensaje": "No puede cancelar un pedido que está en estado CANCELADO"
}
```

... ves una respuesta **HTTP 405 Método no permitido** . **DELETE se** ha convertido en una operación no válida. El `VndError`objeto de respuesta indica claramente que no puede "cancelar" un pedido que ya esté en el estado "CANCELADO".

Además, intentar completar el mismo pedido también falla:

```
$ curl -v -X PUT localhost: 8080 / orders / 4 / complete

* Conjunto TCP_NODELAY
* Conectado al puerto localhost (:: 1) 8080 (# 0)
> PUT / orders / 4 / complete HTTP / 1.1
> Anfitrión: localhost: 8080
> Usuario-Agente: curl / 7.54.0
> Aceptar: * / *
>
<HTTP / 1.1 405
<Tipo de contenido: application / hal + json; charset = UTF-8
<Codificación de transferencia: fragmentada
<Fecha: lunes, 27 de agosto de 2018 15:05:40 GMT
<
{
  "logref": "Método no permitido",
  "mensaje": "No puede completar un pedido que está en estado CANCELADO"
}
```

Con todo esto en su lugar, su servicio de cumplimiento de pedidos es capaz de mostrar condicionalmente qué operaciones están disponibles. También protege contra operaciones inválidas.

Al aprovechar el protocolo de hipermedia y enlaces, los clientes pueden ser más sólidos y menos propensos a romperse simplemente debido a un cambio en los datos. Y Spring HATEOAS facilita la creación del hipermedia que necesita para servir a sus clientes.

## Resumen

A lo largo de este tutorial, ha participado en varias tácticas para construir la API REST. Resulta que REST no se trata solo de URI bonitos y de devolver JSON en lugar de XML.

En cambio, las siguientes tácticas ayudan a que sus servicios sean menos propensos a romper clientes existentes que puede controlar o no:

- No elimines los campos viejos. En cambio, apóyanlos.
- Utilice enlaces basados en rel para que los clientes no tengan que codificar los URI.
- Conserva los enlaces antiguos el mayor tiempo posible. Incluso si tiene que cambiar el URI, conserve los registros para que los clientes más antiguos tengan acceso a las funciones más nuevas.
- Use enlaces, no datos de carga útil, para instruir a los clientes cuando varias operaciones de conducción del estado están disponibles.

Puede parecer un poco difícil construir `ResourceAssembler`implementaciones para cada tipo de recurso y utilizar estos componentes en todos sus controladores. Pero este bit adicional de configuración del lado del servidor (hecho fácil gracias a Spring HATEOAS) puede garantizar que los clientes que controlas (y lo que es más importante, los que no) pueden actualizar con facilidad a medida que evolucionas tu API.

Esto concluye nuestro tutorial sobre cómo construir servicios RESTful usando Spring. Cada sección de este tutorial se administra como un subproyecto separado en un único repositorio de github:

- **nonrest** : aplicación simple Spring MVC sin hipermedia
- **resto** - aplicación Spring MVC + Spring HATEOAS con representaciones HAL de cada recurso
- **evolution** : aplicación REST donde se desarrolla un campo pero los datos antiguos se retienen para la compatibilidad con versiones anteriores
- **enlaces** : aplicación REST donde se usan enlaces condicionales para indicar cambios de estado válidos a los clientes