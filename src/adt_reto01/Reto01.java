/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package adt_reto01;

import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.lang.GeoLocation;
import com.drew.metadata.exif.GpsDirectory;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.ATOMIC_MOVE;
import java.util.Scanner;
import javax.imageio.ImageIO;
import org.imgscalr.Scalr;

/**
 *
 * @author Dan
 */
public class Reto01 {

    public static void main(String[] args) throws IOException, ImageProcessingException {
        Boolean menu = true;
        String fecha = "";
        Scanner sc = new Scanner(System.in);
        int opcion;

        while (menu) {
            System.out.println("1.Organizar imagenes por fecha");
            System.out.println("2.Leer y mostrar coordenadas");
            System.out.println("3.Organizar imagenes por ciudad");
            System.out.println("4.Cambios en imagen");
            System.out.println("5.Salir");
            System.out.println("Introduce una opcion");
            opcion = sc.nextInt();
            sc.nextLine();
            switch (opcion) {

                case 1:
                    System.out.println("Introduce la ruta de la carpeta");
                    String direccion = sc.nextLine();
                    File dir = new File(direccion);
                    if (dir.isDirectory()) {
                        File[] files = dir.listFiles();
                        for (File f : files) {
                            Metadata metadata = ImageMetadataReader.readMetadata(f);
                            for (Directory directory : metadata.getDirectories()) {
                                for (Tag tag : directory.getTags()) {
                                    if (tag.getDescription().contains("2019")) {
                                        String tagString = (String) tag.getDescription();
                                        String anyo = tagString.substring(27, 31);
                                        String mes = tagString.substring(8, 10);
                                        fecha = anyo + mes;
                                        File fichero = new File(direccion + "\\" + fecha);
                                        if (!fichero.exists()) {
                                            fichero.mkdir();
                                            Path source = Paths.get(f.getAbsolutePath());
                                            Path newdir = Paths.get(fichero.getAbsolutePath());
                                            Files.move(source, newdir.resolve(source.getFileName()), ATOMIC_MOVE);
                                        } else {
                                            Path source = Paths.get(f.getAbsolutePath());
                                            Path newdir = Paths.get(fichero.getAbsolutePath());
                                            Files.move(source, newdir.resolve(source.getFileName()), ATOMIC_MOVE);
                                        }
                                    }

                                }
                            }
                        }
                    }

                    break;
                case 2:
                    System.out.println("Introduce la ruta de la carpeta");
                    String direccion2 = sc.nextLine();
                    File dir2 = new File(direccion2);
                    if (dir2.isDirectory()) {
                        File[] files = dir2.listFiles();
                        for (File f : files) {

                            Metadata metadata = ImageMetadataReader.readMetadata(f);
                            for (Directory directory : metadata.getDirectories()) {
                                for (Tag tag : directory.getTags()) {
                                    String tagName = tag.getTagName();
                                    if (tagName == "File Name") {
                                        String nombre = tag.getDescription();
                                        System.out.println("Nombre: " + nombre);
                                    }

                                }
                            }
                            GpsDirectory geoDir = metadata.getFirstDirectoryOfType(GpsDirectory.class);

                            if (geoDir != null) {
                                GeoLocation geoLocation = geoDir.getGeoLocation();
                                System.out.println("Coordenadas: " + geoLocation);

                            } else {

                                System.out.println("Coordenadas: El archivo no contiene coordenadas");
                            }

                        }
                    }

                    break;

                case 3:

                    break;

                case 4:
                    boolean submenu = true;
                    while (submenu) {
                        System.out.println("1.Reducir tamaño");
                        System.out.println("2.Realizar copia con marca de agua");
                        System.out.println("3. Volver al menu anterior");
                        System.out.println("Introduce una opcion");
                        int opcionSub = sc.nextInt();
                        sc.nextLine();

                        switch (opcionSub) {
                            case 1:
                                System.out.println("Introduce la ruta de la carpeta");
                                String direccion3 = sc.nextLine();
                                System.out.println("Introduce el tamaño a escalar");
                                int tamanyo = sc.nextInt();
                                File dir3 = new File(direccion3);
                                if (dir3.isDirectory()) {
                                    File[] files = dir3.listFiles();
                                    for (File f : files) {
                                        BufferedImage srcImage = ImageIO.read(f);
                                        BufferedImage scaledImage = Scalr.resize(srcImage, tamanyo);
                                        File outputfile = new File(f.getAbsolutePath());
                                        ImageIO.write(scaledImage, "jpg", outputfile);

                                    }
                                }
                                break;
                            case 2:
                                System.out.println("Introduce la ruta de la carpeta");
                                String direccion4 = sc.nextLine();
                                File dir4 = new File(direccion4);
                                if (dir4.isDirectory()) {
                                    File[] files = dir4.listFiles();
                                    for (File f : files) {
                                        addTextWatermark("MARCA DE AGUA", f, f);
                                    }
                                }
                                break;
                            case 3:
                                submenu = false;
                        }
                    }

                    break;
                case 5:
                    menu = false;
                    break;
            }

        }

    }

    static void addTextWatermark(String text, File sourceImageFile, File destImageFile) {
        try {
            BufferedImage sourceImage = ImageIO.read(sourceImageFile);
            Graphics2D g2d = (Graphics2D) sourceImage.getGraphics();

            // initializes necessary graphic properties
            AlphaComposite alphaChannel = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);
            g2d.setComposite(alphaChannel);
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 64));
            FontMetrics fontMetrics = g2d.getFontMetrics();
            Rectangle2D rect = fontMetrics.getStringBounds(text, g2d);

            // calculates the coordinate where the String is painted
            int centerX = (sourceImage.getWidth() - (int) rect.getWidth()) / 2;
            int centerY = sourceImage.getHeight() / 2;

            // paints the textual watermark
            g2d.drawString(text, centerX, centerY);

            ImageIO.write(sourceImage, "png", destImageFile);
            g2d.dispose();

            System.out.println("The tex watermark is added to the image.");

        } catch (IOException ex) {
            System.err.println(ex);
        }
    }

}
