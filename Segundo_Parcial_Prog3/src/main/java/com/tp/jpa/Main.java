package com.tp.jpa;

import com.tp.jpa.model.entities.Categoria;
import com.tp.jpa.model.entities.Producto;
import com.tp.jpa.repository.CategoriaRepository;
import com.tp.jpa.repository.ProductoRepository;
import jdk.swing.interop.SwingInterOpUtils;

import java.util.List;

public class Main {
    public static void main(String[] args) {


            CategoriaRepository categoriaRepo = new CategoriaRepository();
            ProductoRepository productoRepo = new ProductoRepository();

            // ============================
            // 1. Crear categoría (y guardarla ANTES)
            // ============================
            Categoria cat = categoriaRepo.guardar(
                    Categoria.builder()
                            .nombre("Electrónica_" + System.currentTimeMillis())
                            .descripcion("Productos electrónicos")
                            .build()
            );

            System.out.println(" ------------------------- TEST 1 ------------------------- ");
            System.out.println("Categoría guardada:");
            System.out.println(cat);

            // ============================
            // 2. Crear productos asociados
            // ============================
            Producto p1 = Producto.builder()
                    .nombre("Mouse Gamer")
                    .precio(15000.0)
                    .descripcion("Mouse RGB 7200 DPI")
                    .stock(10)
                    .imagen("mouse.jpg")
                    .disponible(true)
                    .categoria(cat)
                    .build();

            Producto p2 = Producto.builder()
                    .nombre("Teclado Mecánico")
                    .precio(30000.0)
                    .descripcion("Switch Red")
                    .stock(5)
                    .imagen("teclado.jpg")
                    .disponible(true)
                    .categoria(cat)
                    .build();

            // *** ESTA ES LA CLAVE ***
            p1 = productoRepo.guardar(p1);
            p2 = productoRepo.guardar(p2);

            System.out.println(" ------------------------- TEST 2 ------------------------- ");
            System.out.println("\nProductos guardados correctamente.");
            System.out.println("\nProductos: \n "+ p1 + " \n " + p2);

            // ============================
            // 3. Buscar productos por categoría
            // ============================
            System.out.println(" ------------------------- TEST 3 ------------------------- ");
            System.out.println("\nProductos de la categoría:");
            List<Producto> productos = productoRepo.buscarPorCategoria(cat.getId());
            productos.forEach(System.out::println);

            // ============================
            // 4. Eliminar lógicamente un producto
            // ============================
            boolean eliminado = productoRepo.eliminarLogico(p1.getId());

            if (eliminado) {
                    System.out.println(" ------------------------- TEST 4 ------------------------- ");
                    System.out.println("\nProducto eliminado lógicamente: " + p1.getNombre());
            }

            // ============================
            // 5. Listar productos activos
            // ============================
            System.out.println(" ------------------------- TEST 5 ------------------------- ");
            System.out.println("\nProductos activos:");
            productoRepo.listarActivos().forEach(System.out::println);

            System.out.println("\nFIN DEL TEST");
    }

}
