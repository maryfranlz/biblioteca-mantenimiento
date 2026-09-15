# Reporte de Auditoría y Errores del Proyecto: Biblioteca

**Proyecto:** Biblioteca (Versión Swing / Mantenimiento)  
**Fecha de Análisis:** Septiembre 2026  
**Entorno:** Java SE (Swing / AWT)

---

## Resumen Ejecutivo

Se ha realizado una auditoría exhaustiva del código fuente del proyecto, abarcando las clases de dominio (`Biblioteca`, `Libro`, `Usuario`, `Prestamo`, `CategoriaLibro`), la interfaz gráfica Swing (`BibliotecaUI`, paneles y diálogos modales), clases de soporte y utilidades, así como los programas de demostración y documentación.

Se identificaron **21 problemas y defectos** categorizados en cuatro niveles de impacto:
1. **Errores Críticos y de Integridad de Datos (6)**: Bloqueos de estado permanentes, filtros inoperativos, colisiones de identificadores y fallos de lógica de negocio.
2. **Errores de Interfaz Gráfica y Experiencia de Usuario (8)**: Ventanas fantasma / modales vacíos, falta de reactividad entre paneles, tipografías/typos en UI y representaciones defectuosas de datos.
3. **Robustez, Validación y Manejo de Excepciones (4)**: Riesgos de `NullPointerException`, entradas inválidas no controladas y carga frágil de recursos multimedia.
4. **Mantenibilidad, Arquitectura y Documentación (3)**: Inconsistencia severa entre el código y `README.TXT`, falta de persistencia y ausencia de pruebas unitarias.

---

## 1. Errores Críticos y de Lógica de Negocio

### 1.1. Eliminación de usuarios con préstamos activos (Bloqueo permanente de libros)
* **Archivo afectado:** `Biblioteca.java` (Líneas 311–318) y `UsuariosPanel.java` (Líneas 86–94)
* **Descripción:** El método `eliminarUsuario(String idUsuario)` permite eliminar a un usuario sin comprobar si tiene libros prestados (`!usuario.getLibrosPrestados().isEmpty()`).
* **Impacto:** Si se elimina a un usuario que posee libros en su poder:
  1. El usuario se borra de la lista de la biblioteca.
  2. El libro permanece con `prestado = true`.
  3. Al intentar devolver el libro (`devolverLibro(isbn, idUsuario)`), el método falla inmediatamente porque `buscarUsuarioPorId(idUsuario)` retorna `null`.
  4. El libro queda bloqueado permanentemente: no se puede prestar, ni devolver, ni vender, ni eliminar del sistema.
* **Solución recomendada:**
  ```java
  public boolean eliminarUsuario(String idUsuario) {
      Usuario usuario = buscarUsuarioPorId(idUsuario);
      if (usuario == null || !usuario.getLibrosPrestados().isEmpty()) {
          return false; // No permitir eliminar usuarios con préstamos pendientes
      }
      return usuarios.remove(usuario);
  }
  ```

---

### 1.2. Filtro de categorías inoperativo en el catálogo
* **Archivo afectado:** `LibrosPanel.java` (Líneas 43–48, 83, 87–91)
* **Descripción:** En `LibrosPanel` se agrega un `JComboBox<String> categoria` para filtrar por categorías y se le asigna un listener `categoria.addActionListener(e -> refresh())`. Sin embargo, dentro del método `refresh()`, nunca se lee la opción seleccionada en el combo box:
  ```java
  private void refresh() {
      modelo.setRowCount(0);
      List<Libro> libros = biblioteca.buscarLibros(txtBuscar.getText()); // ¡Ignora la categoría!
      ...
  ```
* **Impacto:** El usuario cambia de categoría en el menú desplegable y no ocurre absolutamente nada; la tabla siempre muestra todos los libros o solo los que coinciden con el texto del buscador.
* **Solución recomendada:** Combinar el filtro de texto con la categoría seleccionada en `refresh()`:
  ```java
  String catSeleccionada = (String) categoria.getSelectedItem();
  List<Libro> libros = biblioteca.buscarLibros(txtBuscar.getText());
  if (catSeleccionada != null && !catSeleccionada.equals("Todas las categorías")) {
      libros = libros.stream()
          .filter(l -> l.getCategoria().name().equalsIgnoreCase(catSeleccionada))
          .toList();
  }
  ```

---

### 1.3. Inconsistencia en la venta de libros (`venderLibro` vs `Libro.isVendido`)
* **Archivo afectado:** `Biblioteca.java` (Líneas 246–258, 283–294) y `Libro.java` (Líneas 16, 122–129, 131–142)
* **Descripción:** `Libro` cuenta con un atributo `vendido`, métodos `vender()` y `getEstadoTexto()` que retorna `"Vendido"`. No obstante, en `Biblioteca.venderLibro(isbn)` se ejecuta:
  ```java
  if (libro.vender()) {
      return libros.remove(libro); // Elimina el libro de la lista
  }
  ```
  Al removerlo completamente de `libros`, el estado `"Vendido"` nunca llega a visualizarse en el catálogo. Además, en `Biblioteca.getLibros(boolean prestados)` existe la condición:
  `if (libro.isPrestado() == prestados && !libro.isVendido())`
  la cual se convierte en código muerto porque ningún libro en la lista tendrá jamás `isVendido() == true`.
* **Solución recomendada:** Decidir una de dos políticas claras:
  - **Opción A (Conservar historial de venta):** No remover el libro de la lista `libros`; solo marcarlo como vendido. En la tabla se verá en estado `"Vendido"` y se deshabilitarán los botones de préstamo/devolución.
  - **Opción B (Baja por venta):** Si vender implica dar de baja del inventario, eliminar los atributos redundantes de `Libro` y aclarar el comportamiento.

---

### 1.4. Permisión de ISBN duplicado al editar un libro
* **Archivo afectado:** `LibroDialog.java` (Líneas 48–65)
* **Descripción:** Cuando se crea un libro nuevo (`libro == null`), el diálogo verifica correctamente que el ISBN no exista:
  ```java
  if (biblioteca.buscarLibroPorIsbn(txtIsbn.getText().trim()) != null) {
      throw new IllegalArgumentException("El ISBN ya existe");
  }
  ```
  Pero en la rama de edición (`else`, cuando `libro != null`):
  ```java
  libro.setIsbn(txtIsbn.getText().trim()); // No valida si el nuevo ISBN ya lo tiene otro libro
  ```
* **Impacto:** Si un usuario edita el Libro A y le asigna el ISBN del Libro B, se generan libros duplicados con el mismo ISBN. Como `buscarLibroPorIsbn` retorna el primero que coincida, las operaciones posteriores sobre el segundo libro quedan inaccesibles o alteran el libro equivocado.
* **Solución recomendada:**
  ```java
  String nuevoIsbn = txtIsbn.getText().trim();
  Libro existente = biblioteca.buscarLibroPorIsbn(nuevoIsbn);
  if (existente != null && existente != libro) {
      throw new IllegalArgumentException("El ISBN ya está asignado a otro libro.");
  }
  ```

---

### 1.5. Riesgo de `NullPointerException` en búsquedas globales
* **Archivo afectado:** `Biblioteca.java` (Líneas 149–154)
* **Descripción:** En `buscarLibros(String texto)`:
  ```java
  if (libro.getIsbn().toLowerCase().contains(busqueda) ||
      libro.getTitulo().toLowerCase().contains(busqueda) ||
      libro.getAutor().toLowerCase().contains(busqueda) ||
      libro.getCategoria().toString().toLowerCase().contains(busqueda))
  ```
  Si por cualquier razón un libro tiene `categoria == null`, `libro.getCategoria().toString()` lanza un `NullPointerException` fulminante que interrumpe la ejecución. De igual forma, si algún campo de texto fuera `null`, `.toLowerCase()` lanzará la misma excepción.
* **Solución recomendada:** Proteger contra nulos:
  ```java
  String catStr = libro.getCategoria() != null ? libro.getCategoria().toString().toLowerCase() : "";
  String titulo = libro.getTitulo() != null ? libro.getTitulo().toLowerCase() : "";
  ...
  ```

---

### 1.6. Error en el script demostrativo `Main.java` (ISBN invertido en devolución)
* **Archivo afectado:** `Main.java` (Línea 74)
* **Descripción:** En la demostración de consola:
  - Se presta el libro `9780307474728` (*Cien años de soledad*) a Ana López (`U001`).
  - Se presta el libro `9788424922498` (*Don Quijote*) a Carlos Ruiz (`U002`).
  - En la línea 74 se intenta devolver:
    ```java
    biblioteca.devolverLibro("9788424922498", "U001")
    ```
  - Se le pide a Ana López devolver el libro de Carlos Ruiz.
* **Impacto:** La devolución falla en la consola con el mensaje `"No se pudo devolver"`, arruinando la prueba que pretendía demostrar un ciclo completo de préstamo y devolución exitoso.
* **Solución recomendada:** Cambiar el ISBN al que realmente tiene asignado Ana López:
  ```java
  biblioteca.devolverLibro("9780307474728", "U001")
  ```

---

## 2. Errores de Interfaz Gráfica (Swing / UX)

### 2.1. Ventanas modales "fantasma" / vacías al fallar validaciones
* **Archivos afectados:** `DevolucionDialog.java` (Líneas 28–31, 38–41) y `ExtensionDialog.java` (Líneas 28–31, 37–40)
* **Descripción:** En los constructores de ambos diálogos, si no existe un préstamo activo o si no hay préstamos elegibles, se muestra un mensaje de alerta con `JOptionPane.showMessageDialog`, se invoca `dispose()` y se ejecuta `return;`.
  Sin embargo, en las clases que los llaman (como `LibrosPanel.java` línea 121–123 y `PrestamosPanel.java` línea 25, 32):
  ```java
  new DevolucionDialog(..., libro, this::refresh).setVisible(true);
  ```
  Al llamar a `.setVisible(true)` sobre una instancia que ya ejecutó `dispose()`, Swing vuelve a hacer visible la ventana, pero como el constructor retornó antes de agregar los componentes (`add(panel)`), aparece una ventana gris, vacía y modal de 440x330 píxeles que bloquea al usuario.
* **Solución recomendada:** No realizar control de flujo con `dispose()` en el constructor. Validar **antes** de instanciar el diálogo, o encapsular la apertura en un método estático que compruebe los prerrequisitos:
  ```java
  // En LibrosPanel / PrestamosPanel:
  Prestamo p = biblioteca.buscarPrestamoActivo(libro);
  if (p == null) {
      JOptionPane.showMessageDialog(this, "El libro no tiene un préstamo activo.");
      return;
  }
  new DevolucionDialog(..., libro, this::refresh).setVisible(true);
  ```

---

### 2.2. Falta de sincronización y reactividad entre paneles
* **Archivos afectados:** `BibliotecaUI.java`, `UsuariosPanel.java`, `PrestamosPanel.java`, `LibrosPanel.java` e `InicioPanel.java`
* **Descripción:**
  1. `InicioPanel` (el dashboard principal con estadísticas) solo es pasado a `LibrosPanel`.
  2. En `PrestamosPanel`, al registrar un préstamo, una devolución o una extensión, únicamente se refresca a sí mismo (`this::refresh`). No se actualizan ni `LibrosPanel` ni `InicioPanel`.
  3. En `UsuariosPanel`, agregar o eliminar usuarios no refresca ningún otro panel.
  4. En `LibrosPanel`, prestar, devolver, extender o eliminar libros no llama a `pnlInicio.actualizar()` (únicamente lo hace "Vender").
* **Impacto:** Las estadísticas del panel "Inicio" (libros totales, disponibles, prestados, saldo por retraso) quedan obsoletas y desincronizadas a medida que el usuario opera el sistema. Si el usuario presta un libro en la pestaña "Préstamos" y luego va a la pestaña "Libros", el libro seguirá apareciendo como "Disponible" hasta que realice una búsqueda o reinicie la app.
* **Solución recomendada:** Implementar un mecanismo de eventos / observador o un callback centralizado en `BibliotecaUI` que actualice los paneles dependientes tras cualquier mutación.

---

### 2.3. Título incorrecto con nombre de variable en `DevolucionDialog`
* **Archivo afectado:** `DevolucionDialog.java` (Línea 12)
* **Descripción:** La llamada al constructor padre tiene el texto:
  ```java
  super(owner, "Devolver libroPreseleccionado", ModalityType.APPLICATION_MODAL);
  ```
  Se copió y pegó literalmente el nombre del identificador del parámetro en lugar de `"Devolver libro"`.

---

### 2.4. Falta ortográfica en la cabecera de la tabla de usuarios
* **Archivo afectado:** `UsuariosPanel.java` (Línea 40)
* **Descripción:** El array de columnas contiene una errata:
  ```java
  String[] columnas = {"ID", "Nombre", "Libors prestados", "Editar", "Eliminar"};
  ```
  Debe decir `"Libros prestados"`.

---

### 2.5. Renderizado defectuoso de la lista de libros en `UsuariosPanel`
* **Archivo afectado:** `UsuariosPanel.java` (Línea 63)
* **Descripción:** Al armar la fila para la tabla:
  ```java
  modelo.addRow(new Object[] {
      usuario.getId(),
      usuario.getNombre(),
      usuario.getLibrosPrestados(), // Pasa un ArrayList<Libro>
      "Editar",
      "Eliminar"
  });
  ```
  En la celda se invoca automáticamente el `toString()` de `ArrayList`, desplegando una cadena cruda ilegible y desbordada como `[Don Quijote de la Mancha - Miguel de Cervantes (FICCION, ISBN: 9788424922498) [Prestado]]`.
* **Solución recomendada:** Mostrar la cantidad de libros (`usuario.getLibrosPrestados().size()`) o una lista formateada de títulos concatenados.

---

### 2.6. Color de botón "Eliminar" inconsistente en `EditorBoton` y `RenderBoton`
* **Archivos afectados:** `EditorBoton.java` (Línea 27) y `RenderBoton.java` (Línea 15)
* **Descripción:** Ambos componentes tienen codificado rígidamente:
  ```java
  setBackground(column == 9 ? new Color(220, 100, 90) : Recursos.COLOR_BOTON);
  ```
  En `LibrosPanel` la columna 9 corresponde efectivamente al botón "Eliminar". Sin embargo, en `UsuariosPanel` el botón "Eliminar" se encuentra en la **columna 4**. Como resultado, el botón de eliminar usuarios no se tiñe de color rojo como fue previsto en el diseño.
* **Solución recomendada:** Evaluar el texto del botón (`"Eliminar".equalsIgnoreCase(boton.getText())`) en lugar del índice de columna.

---

### 2.7. Degradación de rendimiento y recreación de renderers en `LibrosPanel.refresh()`
* **Archivo afectado:** `LibrosPanel.java` (Líneas 107–110)
* **Descripción:** Cada vez que se pulsa una tecla en el campo de búsqueda `txtBuscar`, se ejecuta `refresh()`, y dentro de él se reinstancian y reasignan renderers y editores:
  ```java
  for (int c = 5; c < modelo.getColumnCount(); c++) {
      tablaLibros.getColumnModel().getColumn(c).setCellRenderer(new RenderBoton());
      tablaLibros.getColumnModel().getColumn(c).setCellEditor(new EditorBoton(new JCheckBox(), this::action));
  }
  ```
* **Impacto:** Esto crea cientos de objetos por segundo durante la escritura rápida, provocando sobrecarga en el Garbage Collector y potenciales cancelaciones abruptas del foco de edición de la tabla.
* **Solución recomendada:** Configurar los renderers y editores una sola vez en el constructor de `LibrosPanel`.

---

### 2.8. Disposición deformada de botones en `PrestamosPanel`
* **Archivo afectado:** `PrestamosPanel.java` (Líneas 42–44)
* **Descripción:** Se añaden tres botones a un panel con `BorderLayout`:
  ```java
  pnlSuperior.add(btnExtenderPrestamo, BorderLayout.WEST);
  pnlSuperior.add(btnDevolver, BorderLayout.CENTER);
  pnlSuperior.add(btnPrestar, BorderLayout.EAST);
  ```
  En `BorderLayout`, la zona `CENTER` absorbe todo el espacio horizontal restante. El botón central ("Realizar devolución") se estira desmesuradamente a lo largo de toda la ventana, viéndose desproporcionado respecto a los botones laterales.
* **Solución recomendada:** Utilizar un `FlowLayout(FlowLayout.LEFT, 10, 0)` o un `GridLayout(1, 3, 10, 0)`.

---

## 3. Robustez, Validación y Soporte de Recursos

### 3.1. Carga frágil de imágenes y riesgo con iconos nulos (`Recursos.java`)
* **Archivo afectado:** `Recursos.java` (Líneas 21–25)
* **Descripción:**
  1. `ImageIcon iconoOg = new ImageIcon(iconoFile);` asume que las imágenes (`books.png`, `user.png`, etc.) están en el directorio de trabajo activo de la JVM. Si el proyecto se compila en un `.jar` o se inicia desde otra carpeta, todas las imágenes fallan.
  2. En `PrestamosPanel.java` se llama a `Recursos.crearBoton("Extender préstamo", null)`. Al pasar `null`, se instancia un `ImageIcon(null)`, lo que genera una imagen transparente de 16x16 que desplaza innecesariamente el texto del botón 26 píxeles hacia la derecha.
* **Solución recomendada:**
  - Cargar recursos mediante `Recursos.class.getResource("/" + iconoFile)`.
  - Si `iconoFile == null`, crear el `JButton` únicamente con texto y sin icono.

---

### 3.2. Vulnerabilidad en generación de IDs de usuario (`generarIdUsuario`)
* **Archivo afectado:** `Biblioteca.java` (Líneas 17, 306–309)
* **Descripción:** El método genera IDs con un contador secuencial simple (`contadorUsuarios++` -> `U001`, `U002`...).
  - Si se agregan usuarios con IDs predefinidos (como hace `Main.java` o si vinieran de una base de datos), el contador no se sincroniza.
  - Al invocar `generarIdUsuario()`, se pueden generar IDs que ya existen físicamente en la lista, produciendo colisiones graves de identidad.
  - Además, `agregarUsuario(Usuario u)` no verifica si el ID ya está duplicado.

---

### 3.3. Falta de validación en precios y campos numéricos (`LibroDialog.java`)
* **Archivo afectado:** `LibroDialog.java` (Línea 57, 64)
* **Descripción:** `Double.parseDouble(txtPrecio.getText())` no valida que el precio sea mayor o igual a 0. Permite guardar libros con precios negativos (ej. `-150.0`). Asimismo, si el usuario ingresa texto no numérico, el mensaje de error capturado muestra la excepción técnica de Java (`For input string: "..."`) en vez de un mensaje comprensible.

---

### 3.4. Regla de negocio anómala en extensión de préstamos vencidos (`Prestamo.java`)
* **Archivo afectado:** `Prestamo.java` (Líneas 143–154)
* **Descripción:** `extender()` permite aplicarse a préstamos en estado `RETRASO`. Si un libro tiene 15 días de retraso y se le suman 7 días a la fecha límite original, el préstamo sigue teniendo 8 días de retraso. El usuario gasta su única extensión sin beneficio práctico y continúa en estado de morosidad.
* **Solución recomendada:** No permitir extensiones si el préstamo ya cayó en estado `RETRASO`, o bien recalcular la extensión a partir de la fecha actual (`LocalDate.now().plusDays(7)`).

---

## 4. Inconsistencias de Documentación y Buenas Prácticas

### 4.1. Inconsistencia flagrante entre `README.TXT` y el código real
* **Archivo afectado:** `README.TXT` (Líneas 65–71)
* **Descripción:** El archivo de documentación expresa textualmente:
  ```
  QUE SE QUITO RESPECTO AL PROYECTO ORIGINAL
  ------------------------------------------
    Interfaz grafica (BibliotecaGUI, DialogoNuevoLibro, DialogoUbicacion),
    graficas estadisticas (GraficoPastel, GraficoLineal, ...), la herencia
    ... las multas por retraso, los vencimientos ...
  ```
  Sin embargo, el repositorio contiene actualmente una interfaz gráfica completa con Swing (`BibliotecaUI`), cálculo de recargos por retraso (`recargo = diasRetraso * 10.0`), extensiones de préstamo y venta de libros. El `README.TXT` quedó completamente desactualizado y desorienta a los desarrolladores y evaluadores de la materia de Mantenimiento.

---

### 4.2. Falta de persistencia de datos
* **Descripción:** Todas las entidades se conservan exclusivamente en memoria (`List<Libro>`, `List<Usuario>`, `List<Prestamo>`). Al cerrar la aplicación, todas las altas, bajas, préstamos y ventas se pierden de forma irreversible.
* **Solución sugerida:** Implementar un mecanismo de serialización a archivo (JSON, CSV o SQLite) para mantener la información entre sesiones.

---

### 4.3. Falta de pruebas unitarias automatizadas (JUnit)
* **Descripción:** Siendo un proyecto para la materia de *Mantenimiento de Software*, la ausencia de tests automatizados impide aplicar mantenimiento regresivo con seguridad. Cambiar la lógica de préstamos, retrasos o eliminaciones puede introducir efectos secundarios inadvertidos.

---

## Matriz de Prioridades para Mantenimiento

| Prioridad | ID | Defecto Encontrado | Archivo Principal | Impacto |
|---|---|---|---|---|
| **P1 - Crítico** | 1.1 | Borrado de usuarios con libros prestados | `Biblioteca.java` | Pérdida de integridad, libros bloqueados |
| **P1 - Crítico** | 1.2 | Filtro de categorías ignorado | `LibrosPanel.java` | Funcionalidad rota en UI |
| **P1 - Crítico** | 1.4 | Colisión de ISBN permitida al editar | `LibroDialog.java` | Corrupción de catálogo |
| **P1 - Crítico** | 2.1 | Diálogos vacíos / fantasma con `dispose()` | `DevolucionDialog.java`, `ExtensionDialog.java` | Bloqueo y confusión de usuario |
| **P2 - Alto** | 1.3 | Venta remueve libro en vez de cambiar estado | `Biblioteca.java` | Estado "Vendido" inalcanzable |
| **P2 - Alto** | 2.2 | Falta de actualización entre paneles | `BibliotecaUI.java` | Métricas y tablas desactualizadas |
| **P2 - Alto** | 1.5 | Riesgo de NPE en búsqueda general | `Biblioteca.java` | Caída de aplicación ante datos nulos |
| **P2 - Alto** | 3.1 | Carga de imágenes frágil y botones nulos | `Recursos.java` | Fallos fuera de la raíz, texto desfasado |
| **P3 - Medio** | 2.5 | Renderizado de `List<Libro>` en tabla | `UsuariosPanel.java` | Visualización rota en celdas |
| **P3 - Medio** | 2.6 | Botón "Eliminar" sin color rojo | `UsuariosPanel.java` / `EditorBoton.java` | Inconsistencia de diseño |
| **P3 - Medio** | 2.7 | Recreación masiva de editores de celda | `LibrosPanel.java` | Desperdicio de memoria y ciclos |
| **P3 - Medio** | 1.6 | ISBN incorrecto en demostración `Main` | `Main.java` | Error en demo principal |
| **P3 - Medio** | 3.2 | Colisión de IDs generados para usuarios | `Biblioteca.java` | Inconsistencia de identidad |
| **P3 - Medio** | 3.3 | Falta de validación de precio positivo | `LibroDialog.java` | Datos financieros erróneos |
| **P4 - Bajo** | 2.3 | Título con nombre de parámetro | `DevolucionDialog.java` | Error estético menor |
| **P4 - Bajo** | 2.4 | Errata ortográfica "Libors" | `UsuariosPanel.java` | Error tipográfico en UI |
| **P4 - Bajo** | 2.8 | Botón central sobredimensionado | `PrestamosPanel.java` | Desproporción visual |
| **P4 - Bajo** | 4.1 | Documentación `README.TXT` obsoleta | `README.TXT` | Incoherencia documental |
