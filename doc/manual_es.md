# ShoLi - un sencillo gestor de listas cortas

## Introducción

**ShoLi** pretende ser una herramienta muy sencilla para editar listas cortas y marcar los elementos de esas mismas listas. Su interfaz limpia y eficiente fue inicialmente inspirada en gran medida por la que una vez tuvo [Trolly](http://code.google.com/p/trolly/).

Este documento pretende ser el manual de usuario de **ShoLi**. Los usuarios que tienen prisa pueden revisar el capítulo siguiente donde podrán encontrar cómo instalar la aplicación y a continuación pasar al capítulo **Uso**. Otras partes del documento proporcionan información que los desarrolladores pueden encontrar interesante.

## Instalación

**ShoLi** es software libre y le invitamos a compilarlo y distribuirlo a partir de [sus fuentes](https://github.com/dsoulayrol/android-Sholi). Si prefiere las distribuciones binarias, puede descargarlo desde las siguientes tiendas de aplicaciones:

 * [F-Droid](https://f-droid.org/repository/browse/?fdid=name.soulayrol.rhaa.sholi). Esto es un repositorio dedicado al software libre en dispositivos **Android**. Las aplicaciones son revisadas para detectar malas prácticas, compiladas y firmadas por [ellos](https://f-droid.org/about/).
 * [Google Play](https://play.google.com/store/apps/details?id=name.soulayrol.rhaa.sholi). El repositorio oficial, ofrecido por **Google**. Por defecto sólo las aplicaciones de este repositorio están autorizadas a instalarse en dispositivos **Android**. Por lo tanto, si usted no entiende a que se refiere la línea anterior, sólo tiene que utilizar este enlace.

**ShoLi** está escrita para **Android** 4.0 o superior. Está diseñado para pequeños dispositivos en modo vertical por lo que puede parecer bastante horrible o ser ineficaz en una tableta siendo esto algo normal.

## Uso

### Primer contacto

**ShoLi** mantiene un conjunto de elementos, y esos elementos se utilizan para elaborar listas. Un uso típico consiste en, primero, crear o completar el conjunto de artículos de alimentación y ponerlos en la lista (por lo general en casa, cuando usted se da cuenta de que se va a acabar la leche, por ejemplo) y luego abrir la lista y marcar los elementos cuando se introducen en el carrito de la compra. **ShoLi** no está limitado sólo a listas de alimentación, por lo que pueden crearse listas con cualquier otro contenido.

Cuando **ShoLi** se inicia la primera vez aparece la *vista principal*, que es no resulta útil en este momento debido a que tanto su lista como su conjunto de elementos están vacíos. Al hacer clic en el icono de la pluma se pasa a la *vista de edición*. Allí se puede utilizar el campo de entrada en la parte superior de la pantalla para introducir nuevos elementos. Nótese cómo este campo actúa como un filtro en la vista y que no se puede introducir el mismo nombre del artículo dos veces. A continuación toque un elemento para ponerlo en la lista (pasa a ser de color verde) o eliminarlo de la lista (vuelve a ser de color gris).

Cuando la lista esté completa puede volver a la *vista principal* haciendo clic en el icono de la aplicación o el botón de retroceso de **Android**. Ningún nuevo elemento se puede introducir ahora pero usted puede marcar los que ha añadido previamente tocándolos. En caso de error se restauran con otro toque.

### Acciones

La *vista principal* ofrece algunas acciones que pueden ser útiles para acelerar la interacción con la lista. Al hacer clic en la entrada *Acción* del menú de opciones se muestra un desplegable listando estas acciones. Estas son:

 * *Marcar todos*, para marcar todos los elementos de la lista.
 * *Desmarcar todo*, para desmarcar todos los elementos de la lista.
 * *Eliminar los elementos marcados*, para eliminar todos los elementos que se encuentran marcados de la lista. Los artículos están todavía disponibles en el conjunto de artículos para elegir en la *vista de edición*.
 * *Eliminar todo*, para vaciar la lista. Una vez más, todos los elementos eliminados siguen almacenados por **ShoLi** y se pueden poner en una lista de nuevo desde la *vista de edición*.

### Gestos

**ShoLi** es capaz de reconocer gestos simples en la *vista principal* para hacer la manipulación de la lista más eficiente si se siente cómodo con ellos. La detección de gestos se activa con multi-touch, por lo que son necesarios al menos dos dedos (aunque los toques deberían funcionar también). Actualmente, **ShoLi** capta los movimientos de deslizamiento hacia la izquierda o hacia la derecha, y el doble toque.

Por defecto, el deslizamiento a la izquierda no hace nada, y el deslizamiento a la derecha elimina elementos seleccionados de la lista. Pulsar dos veces siempre abre el menú contextual con las acciones posibles: es un acceso directo para la entrada *Acción* del menú de opciones.

La acción asignada a los deslizamientos se puede configurar en los ajustes (accesible desde el menú de opciones). Puede ser una de las acciones simples que se describen en el capítulo anterior, o una de las más complejas acciones siguientes:

* *Marcar o desmarcar todos* (en este orden). Si al menos un elemento no está marcado, entonces marcar todos los elementos no marcados. Si no, desmarcar todos los elementos.
* *Desmarcar o marcar todos* (en este orden). Esto es lo contrario de la anterior: desmarcar todo si por lo menos un elemento está marcado, o marcar todo.

### Manipulación del conjunto de elementos

**ShoLi** almacena todos los elementos proporcionados por el usuario y no los elimina de forma implícita. En particular, cualquiera que sea la forma en que un elemento se elimina de la lista en la *vista principal*, este está siempre disponible en la *vista de edición* para ser utilizado de nuevo.

La (casi) única manera de deshacerse definitivamente de artículos es un toque largo en la *vista de edición*. Esto nos lleva a una interfaz especial que le permite seleccionar uno o más artículos (el que ha hecho clic para abrir este ya está seleccionado), y luego eliminarlos haciendo clic en el icono de la esquina superior derecha.

En realidad, también es posible eliminar todos los elementos a la vez utilizando la *actividad de resumen de datos*, como se detalla en el siguiente capítulo. Sin embargo, esto no suele ser una buena idea si usted es un simple usuario con una larga lista de elementos memorizados.

### Exportación e importación de datos

#### La actividad de resumen de datos

**ShoLi** no requiere permiso, pero puede depender de otro software instalado en el teléfono gracias al mecanismo de [intents](http://developer.android.com/training/basics/intents/index.html) de **Android**.

La *actividad de resumen de datos* (accesible desde el menú de opciones) proporciona una forma explícita para vaciar toda la base de datos o exportar su contenido. Para lograr esta última opción, se utiliza el intent *SEND*, que es comúnmente soportada por aplicaciones de mensajería (e-mail o SMS), algunos editores de texto y otros. Al hacer clic en el botón *Exportar* se le presentará una lista de aplicaciones capaces de manejar la lista de elementos. Al elegir uno es posible que tenga sus artículos exportados en un archivo en la tarjeta SD o listos para ser enviados por correo electrónico.

Esta actividad también es capaz de recibir un intent *SEND* y por lo tanto importar un conjunto de elementos previamente guardado. Para ello debe utilizar una aplicación capaz de cargar la lista de un archivo, un correo o cualquier otro y enviarlo a **ShoLi**. Dependiendo de la política de importación seleccionada en la configuración el estado de los elementos que ya estaban presentes en la base de datos se deja intacto (los elementos son reportados como *ignorados*) o se actualiza.

A modo de ejemplo, el editor de texto **920** o el cliente de correo **K-9** se sabe que funciona en ambos casos.

#### Formato de datos

Un conjunto de artículos exportados es un archivo de texto. Cada línea es el nombre de un artículo, precedido por un solo carácter que define su estado.

  * `*`: El elemento no está en la lista.
  * `-`: El elemento está en la lista.
  * `+`: El elemento está en la lista y marcado.

Cuando se importan datos sólo las líneas con este formato son analizados, por lo que cualquier tipo de comentarios se pueden añadir en el medio.

## Licencia

Este proyecto se distribuye bajo la [licencia GPLv3](http://www.gnu.org/copyleft/gpl-3.0.html). El archivo README en la distribución fuente menciona otros titulares de derechos de autor.
