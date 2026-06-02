package com.tp.jpa;

import com.tp.jpa.model.entities.Categoria;
import com.tp.jpa.model.entities.Producto;
import com.tp.jpa.repository.CategoriaRepository;
import com.tp.jpa.repository.ProductoRepository;
import jdk.swing.interop.SwingInterOpUtils;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {


            Scanner sc = new Scanner(System.in);

            CategoriaRepository categoriaRepo = new CategoriaRepository();
            ProductoRepository productoRepo = new ProductoRepository();

            int opcion;

            do {
                    System.out.println("\n===== MENÚ PRINCIPAL =====");
                    System.out.println("1. Gestión de Categorías");
                    System.out.println("2. Gestión de Productos");
                    System.out.println("3. Reportes");
                    System.out.println("0. Salir");
                    System.out.print("Seleccione una opción: ");
                    opcion = Integer.parseInt(sc.nextLine());

                    switch (opcion) {

                            case 1 -> menuCategorias(sc, categoriaRepo);

                            case 2 -> menuProductos(sc, productoRepo, categoriaRepo);

                            case 3 -> {
                                    System.out.println("\n--- REPORTES ---");
                                    System.out.println("Aún no implementado.");
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
                    System.out.println("\n===== MENÚ DE CATEGORÍAS =====");
                    System.out.println("1. Alta de categoría");
                    System.out.println("2. Baja lógica de categoría");
                    System.out.println("3. Modificación de categoría");
                    System.out.println("4. Listado de categorías activas");
                    System.out.println("0. Salir");
                    System.out.print("Seleccione una opción: ");
                    opcion = Integer.parseInt(sc.nextLine());

                    switch (opcion) {

                            case 1 -> {
                                    System.out.println("\n--- ALTA DE CATEGORÍA ---");
                                    System.out.print("Nombre: ");
                                    String nombre = sc.nextLine();

                                    //Validacion de nombre duplicado
                                    boolean existe = categoriaRepo.listarActivos()
                                            .stream()
                                            .anyMatch(c -> c.getNombre().equalsIgnoreCase(nombre));

                                    if (existe) {
                                            System.out.println("Ya existe una categoría con ese nombre.");
                                            break;
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

                            case 2 -> {
                                    // BAJA LÓGICA
                                    System.out.println("\n--- BAJA LÓGICA DE CATEGORÍA ---");
                                    System.out.print("Ingrese ID: ");
                                    Long id = Long.parseLong(sc.nextLine());

                                    boolean eliminado = categoriaRepo.eliminarLogico(id);

                                    if (eliminado) {
                                            System.out.println("Categoría eliminada correctamente.");
                                    } else {
                                            System.out.println("No existe una categoría con ese ID.");
                                    }
                            }

                            case 3 -> {
                                    // MODIFICACIÓN
                                    System.out.println("\n--- MODIFICACIÓN DE CATEGORÍA ---");
                                    System.out.print("Ingrese ID: ");
                                    Long id = Long.parseLong(sc.nextLine());

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
                                    if (!nuevoNombre.isBlank()) cat.setNombre(nuevoNombre);

                                    System.out.print("Nueva descripción (ENTER para mantener): ");
                                    String nuevaDesc = sc.nextLine();
                                    if (!nuevaDesc.isBlank()) cat.setDescripcion(nuevaDesc);

                                    categoriaRepo.guardar(cat);

                                    System.out.println("Categoría modificada correctamente.");
                            }

                            case 4 -> {
                                    // LISTADO
                                    System.out.println("\n--- LISTADO DE CATEGORÍAS ACTIVAS ---");

                                    List<Categoria> categorias = categoriaRepo.listarActivos();

                                    if (categorias.isEmpty()) {
                                            System.out.println("No hay categorías activas.");
                                    } else {
                                            categorias.forEach(c ->
                                                    System.out.println("ID: " + c.getId() +
                                                            " | Nombre: " + c.getNombre() +
                                                            " | Descripción: " + c.getDescripcion())
                                            );
                                    }
                            }

                            case 0 -> System.out.println("Saliendo del sistema...");

                            default -> System.out.println("Opción inválida.");
                    }

            } while (opcion != 0);

            sc.close();
    }

        private static void menuProductos(Scanner sc, ProductoRepository productoRepo, CategoriaRepository categoriaRepo) {

                int opcion;

                do {
                        System.out.println("\n===== MENÚ DE PRODUCTOS =====");
                        System.out.println("1. Alta de producto");
                        System.out.println("2. Baja lógica de producto");
                        System.out.println("3. Modificación de producto");
                        System.out.println("4. Listado de productos activos");
                        System.out.println("0. Volver");
                        System.out.print("Seleccione una opción: ");
                        opcion = Integer.parseInt(sc.nextLine());

                        switch (opcion) {

                                case 1 -> {
                                        System.out.println("\n--- ALTA DE PRODUCTO ---");

                                        List<Categoria> categorias = categoriaRepo.listarActivos();

                                        if (categorias.isEmpty()) {
                                                System.out.println("No hay categorías activas. No se puede crear un producto.");
                                                break;
                                        }

                                        System.out.println("Categorías disponibles:");
                                        categorias.forEach(c ->
                                                System.out.println("ID: " + c.getId() + " | " + c.getNombre())
                                        );

                                        //verificar long
                                        System.out.print("Seleccione ID de categoría: ");
                                        Long idCat = leerLongSeguro(sc, "Seleccione ID de categoría: ");

                                        Optional<Categoria> optionalCat = categoriaRepo.buscarPorId(idCat);

                                        if (optionalCat.isEmpty() || optionalCat.get().isEliminado()) {
                                                System.out.println("Categoría inválida.");
                                                break;
                                        }

                                        Categoria categoria = optionalCat.get();

                                        System.out.print("Nombre: ");
                                        String nombre = sc.nextLine();

                                        // VALIDACIÓN DE NOMBRE DUPLICADO
                                        List<Producto> existentes = productoRepo.buscarPorNombre(nombre);
                                        if (!existentes.isEmpty()) {
                                                System.out.println("Ya existe un producto con ese nombre.");
                                                break;
                                        }

                                        System.out.print("Descripción: ");
                                        String descripcion = sc.nextLine();

                                        System.out.print("Precio: ");
                                        double precio = Double.parseDouble(sc.nextLine());

                                        if (precio <= 0) {
                                                System.out.println("El precio debe ser mayor a 0.");
                                                break;
                                        }

                                        System.out.print("Stock: ");
                                        int stock = Integer.parseInt(sc.nextLine());

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

                                case 2 -> {
                                        System.out.println("\n--- BAJA LÓGICA DE PRODUCTO ---");

                                        System.out.print("Ingrese ID del producto: ");
                                        Long id = Long.parseLong(sc.nextLine());

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

                                case 3 -> {
                                        System.out.println("\n--- MODIFICACIÓN DE PRODUCTO ---");

                                        List<Producto> productos = productoRepo.listarActivos();

                                        if (productos.isEmpty()) {
                                                System.out.println("No hay productos activos.");
                                                break;
                                        }

                                        System.out.println("Productos activos:");
                                        productos.forEach(p ->
                                                System.out.println("ID: " + p.getId() + " | " + p.getNombre() +
                                                        " | Precio: " + p.getPrecio() + " | Stock: " + p.getStock())
                                        );

                                        System.out.print("Ingrese ID del producto: ");
                                        Long id = Long.parseLong(sc.nextLine());

                                        Optional<Producto> optionalProd = productoRepo.buscarPorId(id);

                                        if (optionalProd.isEmpty() || optionalProd.get().isEliminado()) {
                                                System.out.println("No existe un producto activo con ese ID.");
                                                break;
                                        }

                                        Producto prod = optionalProd.get();

                                        System.out.println("Valores actuales:");
                                        System.out.println("Nombre: " + prod.getNombre());
                                        System.out.println("Precio: " + prod.getPrecio());
                                        System.out.println("Stock: " + prod.getStock());

                                        System.out.print("Nuevo nombre (ENTER para mantener): ");
                                        String nuevoNombre = sc.nextLine();
                                        if (!nuevoNombre.isBlank()) prod.setNombre(nuevoNombre);
                                        // VALIDACIÓN DE NOMBRE DUPLICADO
                                        List<Producto> existentes = productoRepo.buscarPorNombre(nuevoNombre);
                                        if (!existentes.isEmpty()) {
                                                System.out.println("Ya existe un producto con ese nombre.");
                                                break;
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

                                case 4 -> {
                                        System.out.println("\n--- LISTADO DE PRODUCTOS ACTIVOS ---");

                                        List<Producto> productos = productoRepo.listarActivos();

                                        if (productos.isEmpty()) {
                                                System.out.println("No hay productos activos.");
                                        } else {
                                                productos.forEach(p ->
                                                        System.out.println("ID: " + p.getId() +
                                                                " | Nombre: " + p.getNombre() +
                                                                " | Precio: " + p.getPrecio() +
                                                                " | Stock: " + p.getStock() +
                                                                " | Categoría: " + p.getCategoria().getNombre())
                                                );
                                        }
                                }




                                case 0 -> System.out.println("Volviendo al menú principal...");
                                default -> System.out.println("Opción inválida.");
                        }

                } while (opcion != 0);
        }
        private static Long leerLongSeguro(Scanner sc, String mensaje) {
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


}


