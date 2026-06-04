package com.tp.jpa;

import com.tp.jpa.model.entities.Categoria;
import com.tp.jpa.model.entities.Producto;
import com.tp.jpa.repository.CategoriaRepository;
import com.tp.jpa.repository.ProductoRepository;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.logging.Level;


public class Main {
    public static void main(String[] args) {


        //quta los logs de hibernate dejando solo los errores.
        java.util.logging.Logger.getLogger("org.hibernate").setLevel(Level.SEVERE);

        Scanner sc = new Scanner(System.in);

        CategoriaRepository categoriaRepo = new CategoriaRepository();
        ProductoRepository productoRepo = new ProductoRepository();

        int opcion;

        do {
            System.out.println("\n");
            System.out.println("===== MENÚ PRINCIPAL =====\n..........................");
            System.out.println("1. Gestión de Categorías");
            System.out.println("2. Gestión de Productos");
            System.out.println("3. Reportes");
            System.out.println("0. Salir");
            System.out.println("..........................\n");

            opcion = intSeguro(sc, "Ingrese un numero: ");

            switch (opcion) {

                case 1 -> menuCategorias(sc, categoriaRepo);

                case 2 -> menuProductos(sc, productoRepo, categoriaRepo);

                case 3 -> {

                    System.out.println("\n--- REPORTES ---");
                    System.out.println("Elija una opcion: ");

                    System.out.println("1. Buscar Productos por categoria");
                    System.out.println("0. Volver al menu principal");

                    int opcionReporte = intSeguro(sc, "Ingrese un numero: \n");

                    switch (opcionReporte) {
                        case 1 -> {

                            mostrarCategoriasActivas(categoriaRepo);

                            Long idReporte = LongSeguro(sc, "\nSeleccione ID de categoría: ");

                            List<Producto> productos = productoRepo.buscarPorCategoria(idReporte);

                            if (productos.stream().findAny().isEmpty()) {
                                System.out.println("No hay productos activos.");
                            } else {
                                System.out.println("Productos de la categoria: ");
                                productos.forEach(p ->
                                        System.out.println(
                                                "ID: " + p.getId() +
                                                        " | Nombre: " + p.getNombre() +
                                                        " | Precio: " + p.getPrecio() +
                                                        " | Descripcion: " + p.getDescripcion() +
                                                        " | Stock: " + p.getStock() +
                                                        " | Categoría: " + p.getCategoria().getNombre()
                                        )
                                );}

                        }
                        case 0 -> System.out.println("Volviendo al menú principal...");

                    }

                }

                case 0 -> System.out.println("Saliendo del sistema...");

                default -> System.out.println("Opción inválida.");
            }

        } while (opcion != 0);

        sc.close();
    }

    private static void menuCategorias(Scanner sc, CategoriaRepository categoriaRepo) {

        int opcion;

        do {
            System.out.println("\n");
            System.out.println("\n===== MENÚ DE CATEGORÍAS =====");
            System.out.println("1. Alta de categoría");
            System.out.println("2. Baja lógica de categoría");
            System.out.println("3. Modificación de categoría");
            System.out.println("4. Listado de categorías activas");
            System.out.println("0. Volver al Menu Principal");
            System.out.println("..........................\n");

            opcion = intSeguro(sc, "Ingrese un numero: ");


            switch (opcion) {

                case 1 -> { //Alta de categoría
                    System.out.println("\n--- ALTA DE CATEGORÍA ---");
                    System.out.print("Nombre: ");
                    String nombre = sc.nextLine();

                    if (validarNombreCategoria(nombre, categoriaRepo, sc)) {
                        break; // salir del case sin pedir descripción
                    }

                    System.out.print("Descripción: ");
                    String descripcion = sc.nextLine();

                    Categoria nueva = Categoria.builder()
                            .nombre(nombre)
                            .descripcion(descripcion)
                            .build();

                    nueva = categoriaRepo.guardar(nueva);

                    System.out.println("Categoría creada con ID: " + nueva.getId());
                }

                case 2 -> { //Baja logica de Categoria
                    System.out.println("\n--- BAJA LÓGICA DE CATEGORÍA ---");

                    mostrarCategoriasActivas(categoriaRepo);

                    System.out.print("Elija una categoria ");

                    //ingreso seguro de long
                    Long id = LongSeguro(sc, "Seleccione ID:");


                    boolean eliminado = categoriaRepo.eliminarLogico(id);

                    if (eliminado) {
                        System.out.println("Categoría eliminada correctamente.");
                    } else {
                        System.out.println("No existe una categoría con ese ID.");
                    }
                }

                case 3 -> { //modificar categoria
                    System.out.println("\n--- MODIFICACIÓN DE CATEGORÍA ---");

                    mostrarCategoriasActivas(categoriaRepo);

                    //revisar

                    Long id = LongSeguro(sc, "Ingrese ID para modificar la categoria: ");

                    Optional<Categoria> optionalCat = categoriaRepo.buscarPorId(id);

                    if (optionalCat.isEmpty() || optionalCat.get().isEliminado()) {
                        System.out.println("No existe una categoría activa con ese ID.");
                        break;
                    }

                    Categoria cat = optionalCat.get();

                    System.out.println("Valores actuales:");
                    System.out.println("Nombre: " + cat.getNombre());
                    System.out.println("Descripción: " + cat.getDescripcion());

                    System.out.print("Nuevo nombre (ENTER para mantener): ");
                    String nuevoNombre = sc.nextLine();

                    if (validarNombreCategoria(nuevoNombre, categoriaRepo, sc)) {
                        break; // salir del case sin pedir descripción
                    }

                    if (!nuevoNombre.isBlank()) cat.setNombre(nuevoNombre);

                    System.out.print("Nueva descripción (ENTER para mantener): ");
                    String nuevaDesc = sc.nextLine();
                    if (!nuevaDesc.isBlank()) cat.setDescripcion(nuevaDesc);

                    categoriaRepo.guardar(cat);

                    System.out.println("Categoría modificada correctamente.");
                }

                case 4 -> {
                    // Listar categorias
                    System.out.println("\n--- LISTADO DE CATEGORÍAS ACTIVAS ---");

                    mostrarCategoriasActivas(categoriaRepo);

                }


                case 0 -> System.out.println("Volviendo al menú principal...");
                default -> System.out.println("Opción inválida.");
            }


        } while (opcion != 0);

    }

    private static void menuProductos(Scanner sc, ProductoRepository productoRepo, CategoriaRepository categoriaRepo) {

        int opcion;

        do {
            System.out.println("\n");
            System.out.println("\n===== MENÚ DE PRODUCTOS =====");
            System.out.println("1. Alta de producto");
            System.out.println("2. Baja lógica de producto");
            System.out.println("3. Modificación de producto");
            System.out.println("4. Listado de productos activos");
            System.out.println("0. Volver al Menu Principal");
            System.out.println("..........................\n");
            opcion = intSeguro(sc, "Ingrese un numero: ");

            switch (opcion) {

                case 1 -> { //Alta de producto
                    System.out.println("\n--- ALTA DE PRODUCTO ---");

                    mostrarCategoriasActivas(categoriaRepo);

                    System.out.print("Seleccione ID de categoría: ");
                    //verificar long
                    Long idCat = LongSeguro(sc, "Seleccione ID de categoría: ");

                    Optional<Categoria> optionalCat = categoriaRepo.buscarPorId(idCat);


                    if (optionalCat.isEmpty() || optionalCat.get().isEliminado()) {
                        System.out.println("Categoría inválida.");
                        break;
                    }

                    Categoria categoria = optionalCat.get();

                    System.out.print("Nombre: ");
                    String nombre = sc.nextLine();

                    if (validarNombreProducto(nombre, productoRepo, sc)) {
                        break; // salir del case sin pedir descripción
                    }

//
                    System.out.print("Descripción: ");
                    String descripcion = sc.nextLine();

                    System.out.print("Precio: ");
                    double precio = DoubleSeguro(sc, "Ingrese el precio: ");

                    if (precio <= 0) {
                        System.out.println("El precio debe ser mayor a 0.");
                        break;
                    }

                    System.out.print("Stock: ");
                    int stock = intSeguro(sc, "Ingrese el stock: ");

                    if (stock < 0) {
                        System.out.println("El stock no puede ser negativo.");
                        break;
                    }

                    Producto nuevo = Producto.builder()
                            .nombre(nombre)
                            .descripcion(descripcion)
                            .precio(precio)
                            .stock(stock)
                            .disponible(true)
                            .categoria(categoria)
                            .build();

                    nuevo = productoRepo.guardar(nuevo);

                    System.out.println("Producto creado con ID: " + nuevo.getId());
                    System.out.println("Categoría asignada: " + categoria.getNombre());
                }

                case 2 -> { //Baja lógica de producto
                    System.out.println("\n--- BAJA LÓGICA DE PRODUCTO ---");

                    mostrarProductosActivos(productoRepo);

                    Long id = LongSeguro(sc, "Seleccione ID de producto: ");

                    Optional<Producto> optionalProd = productoRepo.buscarPorId(id);

                    if (optionalProd.isEmpty() || optionalProd.get().isEliminado()) {
                        System.out.println("No existe un producto activo con ese ID.");
                        break;
                    }

                    Producto prod = optionalProd.get();

                    boolean eliminado = productoRepo.eliminarLogico(id);

                    if (eliminado) {
                        System.out.println("Producto dado de baja: " + prod.getNombre());
                    } else {
                        System.out.println("No se pudo eliminar el producto.");
                    }
                }

                case 3 -> { //Modificación de producto
                    System.out.println("\n--- MODIFICACIÓN DE PRODUCTO ---");

                    mostrarProductosActivos(productoRepo);

                    Long id = LongSeguro(sc, "Seleccione ID de producto: ");

                    Optional<Producto> optionalProd = productoRepo.buscarPorId(id);

                    if (optionalProd.isEmpty() || optionalProd.get().isEliminado()) {
                        System.out.println("No existe un producto activo con ese ID.");
                        break;
                    }

                    Producto prod = optionalProd.get();

                    System.out.println("Valores actuales:");
                    System.out.println("Nombre: " + prod.getNombre());
                    System.out.println("Precio: " + prod.getPrecio());
                    System.out.println("Descripción: " + prod.getDescripcion());
                    System.out.println("Stock: " + prod.getStock());

                    System.out.print("Nuevo nombre (ENTER para mantener): ");
                    String nuevoNombre = sc.nextLine();
                    if (!nuevoNombre.isBlank()) prod.setNombre(nuevoNombre);

                    if (validarNombreProducto(nuevoNombre, productoRepo, sc)) {
                        break; // salir del case sin pedir descripción
                    }

                    System.out.print("Nuevo precio (ENTER para mantener): ");
                    String nuevoPrecio = sc.nextLine();
                    if (!nuevoPrecio.isBlank()) {
                        double precio = Double.parseDouble(nuevoPrecio);
                        if (precio <= 0) {
                            System.out.println("El precio debe ser mayor a 0.");
                            break;
                        }
                        prod.setPrecio(precio);
                    }

                    System.out.print("Nueva descripción (ENTER para mantener): ");
                    String nuevaDescripcion = sc.nextLine();
                    if (!nuevaDescripcion.isBlank()) prod.setNombre(nuevaDescripcion);


                    System.out.print("Nuevo stock (ENTER para mantener): ");
                    String nuevoStock = sc.nextLine();
                    if (!nuevoStock.isBlank()) {
                        int stock = Integer.parseInt(nuevoStock);
                        if (stock < 0) {
                            System.out.println("El stock no puede ser negativo.");
                            break;
                        }
                        prod.setStock(stock);
                    }

                    productoRepo.guardar(prod);

                    System.out.println("Producto modificado correctamente.");
                }

                case 4 -> { //Listado de productos activos
                    System.out.println("\n--- LISTADO DE PRODUCTOS ACTIVOS ---");

                    mostrarProductosActivos(productoRepo);

                }


            // Volver
            case 0 -> System.out.println("Volviendo al menú principal...");
            default -> System.out.println("Opción inválida.");
        }

    } while(opcion !=0);
}

private static Double DoubleSeguro(Scanner sc, String mensaje) {
    while (true) {
        System.out.print(mensaje);
        String input = sc.nextLine();

        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException e) {
            System.out.println("Debe ingresar un número válido.");
        }
    }
}

private static Long LongSeguro(Scanner sc, String mensaje) {
    while (true) {
        System.out.print(mensaje);
        String input = sc.nextLine();

        try {
            return Long.parseLong(input);
        } catch (NumberFormatException e) {
            System.out.println("Debe ingresar un número válido.");
        }
    }
}

private static int intSeguro(Scanner sc, String mensaje) {
    while (true) {
        System.out.print(mensaje);
        String input = sc.nextLine();

        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Debe ingresar un número válido.");
        }
    }
}

private static boolean validarNombreCategoria(String nombre, CategoriaRepository categoriaRepo, Scanner sc) {
    //Validacion de nombre duplicado
    boolean existe = categoriaRepo.listarActivos()
            .stream()
            .anyMatch(c -> c.getNombre().equalsIgnoreCase(nombre));

    Optional<Categoria> categoriaInactiva = categoriaRepo.listarInactivos()
            .stream()
            .filter(c -> c.getNombre().equalsIgnoreCase(nombre))
            .findFirst();

    //verificacion de categoria repetida
    if (existe) {
        System.out.println("Ya existe una categoría con ese nombre.");
        return true; // no continua
    }
    //verificacion de categoria existente pero con baja logica
    if (categoriaInactiva.isPresent()) {
        System.out.println("Ya existe una categoría con ese nombre, pero se encuentra inactiva" +
                "\n ¿Desea activarla nuevamente");
        System.out.println("1. Si");
        System.out.println("2. No");
        int opcionAlta = intSeguro(sc, "Seleccione una opción: ");

        switch (opcionAlta) {
            case 1 -> {
                Categoria cat = categoriaInactiva.get();
                categoriaRepo.AltaLogica(cat.getId());
                System.out.println("Categoría reactivada correctamente. ID: " + cat.getId());
                return true;
            }
            case 2 -> {
                System.out.println("Operación cancelada.");
                return true; //no continua
            }

        }

    }
    return false; //si pasa las evaluaciones continua con el alta
}

private static void mostrarCategoriasActivas(CategoriaRepository categoriaRepo) {

        List<Categoria> categorias = categoriaRepo.listarActivos();

    if (categorias.stream().findAny().isEmpty()) {
        System.out.println("No hay categorías activas.");
    } else {
        System.out.println("Categorias disponibles :");
        categorias.forEach(cat ->
                System.out.println(
                        "ID: " + cat.getId() +
                                " | Nombre: " + cat.getNombre() +
                                " | Descripcion: " + cat.getDescripcion()
                )
        );}
}

private static void mostrarProductosActivos(ProductoRepository productoRepo) {

        List <Producto> productos = productoRepo.listarActivos();

    if (productos.stream().findAny().isEmpty()) {
        System.out.println("No hay productos activos.");
    } else {
        productos.forEach(p ->
                System.out.println(
                        "ID: " + p.getId() +
                                " | Nombre: " + p.getNombre() +
                                " | Precio: " + p.getPrecio() +
                                " | Descripcion: " + p.getDescripcion() +
                                " | Stock: " + p.getStock() +
                                " | Categoría: " + p.getCategoria().getNombre()
                )
        );
    }
}

private static boolean validarNombreProducto(String nombre, ProductoRepository productoRepo, Scanner sc) {
    boolean existe = productoRepo.listarActivos()
            .stream()
            .anyMatch(c -> c.getNombre().equalsIgnoreCase(nombre));

    Optional<Producto> prodInactivo = productoRepo.listarInactivos()
            .stream()
            .filter(c -> c.getNombre().equalsIgnoreCase(nombre))
            .findFirst();

    //verificacion de producto repetido
    if (existe) {
        System.out.println("Ya existe un producto con ese nombre.");
        return true; // no continua
    }

    //verificacion de producto existente pero con baja logica
    if (prodInactivo.isPresent()) {
        System.out.println("Ya existe un producto con ese nombre, pero se encuentra inactivo" +
                "\n ¿Desea activarlo nuevamente");//
        System.out.println("1. Si");
        System.out.println("2. No");
        int opcionAltaProd = intSeguro(sc, "Seleccione una opción: ");

        switch (opcionAltaProd) {
            case 1 -> {
                Producto prod = prodInactivo.get();
                productoRepo.AltaLogica(prod.getId());
                System.out.println("Producto reactivado correctamente. ID: " + prod.getId());
                return true; // no continua
            }
            case 2 -> {
                System.out.println("Operación cancelada.");
                return true; // no continua
            }
        }


    }
    return false; //si pasa las evaluaciones continua con el alta
}}





