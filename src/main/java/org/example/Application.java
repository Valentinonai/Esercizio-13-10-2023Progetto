package org.example;


import com.github.javafaker.Faker;
import com.sun.tools.javac.Main;
import org.apache.commons.io.FileUtils;
import org.example.entities.Book;
import org.example.entities.Item;
import org.example.entities.Magazine;
import org.example.entities.Periodicita;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

public class Application {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final Faker fkr = new Faker();
    private static final Random rnd = new Random();
    private static final Scanner input = new Scanner(System.in);

    public static void main(String[] args) {

        List<Item> itemList = new ArrayList<>();
        Set<String> isbn = new HashSet<>();
        boolean bool = false;

        File file = new File(("src/main/java/org.example/file.txt"));

        while (!bool) {
            logger.info("1:Crea nuova lista 2:Carica lista");
            try {
                int risp = Integer.parseInt(input.nextLine());
                if (risp == 1) {
                    itemList.addAll(fillItem(isbn));
                    bool = true;
                } else {
                    try {
                        itemList.addAll(fillItemList(file));
                    } catch (IOException e) {
                        logger.error("File non caricato");
                        itemList.addAll(fillItem(isbn));
                        logger.info("Generata nuova lista");
                    } finally {
                        bool = true;
                    }
                }

            } catch (NumberFormatException e) {
                logger.error("Inserisci un numero");
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }
        bool = false;

        Exit:
        while (true) {
            try {
                while (!bool) {
                    logger.info("1:Aggiungi elemento 2:Rimuovi elemento 3:Ricerca ISBN 4:Riccerca per anno 5:Ricerca per autore 6:salva su disco 7:Stampa liste 0:Esci");
                    int risp = Integer.parseInt(input.nextLine());
                    switch (risp) {
                        case 1: {
                            Item l = addItem(isbn);
                            if (l != null) {
                                itemList.add(l);
                            }
                            break;
                        }
                        case 2: {
                            logger.info("Inserisci codice ISBN da eliminare");
                            String cod = input.nextLine();
                            deleteItem(isbn, cod, itemList);
                            break;
                        }
                        case 3: {
                            logger.info("Inserisci codice ISBN da cercare");
                            String cod = input.nextLine();
                            Item found = findItem(cod, itemList);
                            if (found == null) {
                                logger.info("Elemento non trovato");

                            } else logger.info("Elemento trovato: " + found);
                            break;

                        }
                        case 4: {
                            logger.info("Inserisci anno di pubblicazione da cercare");
                            int anno = Integer.parseInt(input.nextLine());
                            List<Item> found = findItemsByYear(anno, itemList);
                            if (found == null) {
                                logger.info("Elemento non trovato");

                            } else logger.info("Elemento trovato: " + found);
                            break;

                        }
                        case 5: {
                            logger.info("Inserisci autore da cercare");
                            String author = input.nextLine();
                            List<Book> found = findItemsByAuthor(author, itemList);
                            if (found == null) {
                                logger.info("Elemento non trovato");

                            } else logger.info("Elemento trovato: " + found);
                            break;

                        }
                        case 6: {
                            saveItemsList(itemList, file);
                            break;
                        }
                        case 7: {
                            printList(itemList);
                            break;
                        }
                        case 0:
                            return;

                    }

                }
            } catch (NumberFormatException e) {
                logger.error("Valore non valido");
            } catch (Exception e) {
                logger.error(e.getMessage());
            }
        }

    }

    public static List<Item> fillItem(Set<String> isbn) {
        List<Item> listItem = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            String cod;
            while (true) {
                cod = String.valueOf(Math.abs(rnd.nextInt()));
                if (isbn.add(cod)) {
                    break;
                }
            }

            if (rnd.nextInt(0, 2) == 0) {
                listItem.add(new Book(cod, fkr.book().title(), rnd.nextInt(1870, 2023), rnd.nextInt(20, 1500), fkr.book().author(), fkr.book().genre()));
            } else {
                int p = rnd.nextInt(0, 3);
                listItem.add(new Magazine(cod, fkr.book().title(), rnd.nextInt(1870, 2023), rnd.nextInt(10, 80), p == 0 ? Periodicita.SETTIMANALE : p == 1 ? Periodicita.MENSILE : Periodicita.SEMESTRALE));
            }
        }
        return listItem;
    }

    public static Item addItem(Set<String> isbn) {

        try {
            boolean bool = false;
            String cod = "";
            String titolo;
            int annoPubblicazione = 0;
            int pagine = 0;
            logger.info("1:Aggiungi libro 2:Aggiungi rivista 0:Menu");
            int n = Integer.parseInt(input.nextLine());
            if (n == 0) {
                return null;
            } else {
                while (!bool) {
                    logger.info("Inserisci codice isbn univoco");
                    cod = input.nextLine();
                    if (isbn.add(cod)) {
                        bool = true;
                    } else logger.error("Codice esiste già");
                }
                bool = false;
                logger.info("Inserisci il titolo");
                titolo = input.nextLine();
                while (!bool) {
                    logger.info("Inserisci anno di pubblicazione");
                    try {
                        annoPubblicazione = Integer.parseInt(input.nextLine());
                        if (annoPubblicazione < 0)
                            throw new Exception("Anno di pubblicazione minore di zero non valido");
                        bool = true;
                    } catch (NumberFormatException e) {
                        logger.error("Non hai inserito un numero");
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }


                }
                bool = false;
                while (!bool) {
                    logger.info("Inserisci numero di pagine");
                    try {
                        pagine = Integer.parseInt(input.nextLine());
                        if (pagine < 0)
                            throw new Exception("Numero pagine minore di zero non valido");
                        bool = true;
                    } catch (NumberFormatException e) {
                        logger.error("Non hai inserito un numero");
                    } catch (Exception e) {
                        logger.error(e.getMessage());
                    }


                }
                bool = false;
                switch (n) {

                    case 1: {
                        logger.info("Inserisci autore");
                        String author = input.nextLine();
                        logger.info("Inserisci genere");
                        String genre = input.nextLine();
                        return new Book(cod, titolo, annoPubblicazione, pagine, author, genre);
                    }
                    case 2: {
                        while (true) {
                            logger.info("Inserisci periodicità 1:SETTIMANALE 2:MENSILE 3:SEMESTRALE");
                            try {
                                int p = Integer.parseInt(input.nextLine());
                                switch (p) {
                                    case 1: {
                                        return new Magazine(cod, titolo, annoPubblicazione, pagine, Periodicita.SETTIMANALE);
                                    }
                                    case 2: {
                                        return new Magazine(cod, titolo, annoPubblicazione, pagine, Periodicita.MENSILE);
                                    }
                                    case 3: {
                                        return new Magazine(cod, titolo, annoPubblicazione, pagine, Periodicita.SEMESTRALE);
                                    }
                                }

                            } catch (NumberFormatException e) {
                                logger.error("Formato numero non valido");
                            } catch (Exception e) {
                                logger.error(e.getMessage());
                            }
                        }

                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return null;
    }

    public static void deleteItem(Set<String> isbn, String cod, List<Item> itemList) {
        Iterator<Item> i = itemList.iterator();
        boolean bool = false;
        while (i.hasNext()) {
            Item current = i.next();
            if (current.getIsbn().equals(cod)) {
                i.remove();
                bool = true;
            }


        }
        if (bool == true) {
            Iterator<String> p = isbn.iterator();
            while (p.hasNext()) {
                String current = p.next();
                if (current.equals(cod))
                    p.remove();
            }
            logger.info("Elemento eliminato");
        } else logger.info("Elemento non trovato");


    }

    public static Item findItem(String cod, List<Item> itemList) {
        return itemList.stream().filter(elem -> elem.getIsbn().equals(cod)).findFirst().orElse(null);
    }

    public static List<Item> findItemsByYear(int anno, List<Item> itemList) {
        Map<Integer, List<Item>> mapItem = itemList.stream().collect(Collectors.groupingBy(Item::getAnnoPubblicazione));
        return mapItem.get(anno);
    }

    public static List<Book> findItemsByAuthor(String author, List<Item> itemList) {
        List<Book> bookList = itemList.stream().map(elem -> {
            if (elem instanceof Book) return (Book) elem;

            return null;
        }).filter(elem -> elem != null).toList();
        Map<String, List<Book>> mapBook = bookList.stream().collect(Collectors.groupingBy(Book::getAutore));

        return mapBook.get(author);
    }

    public static void saveItemsList(List<Item> itemList, File file) throws IOException {
        FileUtils.write(file, "", StandardCharsets.UTF_8);
        itemList.forEach(elem -> {
            String str = elem.getIsbn() + "@" + elem.getTitolo() + "@" + elem.getAnnoPubblicazione() + "@" + elem.getNumeroPagine() + "@";
            if (elem instanceof Book) {
                str += ((Book) elem).getAutore() + "@" + ((Book) elem).getGenere() + System.lineSeparator();
            } else str += ((Magazine) elem).getPeriodicita() + System.lineSeparator();

            try {
                FileUtils.write(file, str, StandardCharsets.UTF_8, true);
            } catch (IOException e) {
                logger.error(e.getMessage());
            }
        });
        logger.info("Salvataggio eseguito con successo");
    }

    public static List<Item> fillItemList(File file) throws IOException {
        List<Item> list = new ArrayList<>();
        String f = FileUtils.readFileToString(file, StandardCharsets.UTF_8);

        List<String> app = new ArrayList<>(List.of(f.split(System.lineSeparator())));

        for (int i = 0; i < app.size(); i++) {
            List<String> str = new ArrayList<>(List.of(app.get(i).split("@")));
            if (str.size() == 6)
                list.add(new Book(str.get(0), str.get(1), Integer.parseInt(str.get(2)), Integer.parseInt(str.get(3)), str.get(4), str.get(5)));
            else {
                list.add(new Magazine(str.get(0), str.get(1), Integer.parseInt(str.get(2)), Integer.parseInt(str.get(3)), str.get(4).equals("SETTIMANALE") ? Periodicita.SETTIMANALE : str.get(4).equals("MENSILE") ? Periodicita.MENSILE : Periodicita.SEMESTRALE));
            }
        }
        return list;
    }

    public static void printList(List<Item> itemList) {
        System.out.println("*********************************BOOKS**********************************");
        itemList.stream().filter(elem -> elem instanceof Book).forEach(elem -> System.out.println(String.valueOf(elem)));
        System.out.println("*********************************MAGAZINES******************************");
        itemList.stream().filter(elem -> elem instanceof Magazine).forEach(elem -> System.out.println(String.valueOf(elem)));
    }

}
