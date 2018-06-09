import java.io.*;
import java.util.*;

public class Subway {
    public static void main(String args[]) {
        HashMap<String, LinkedList<Pair<String, Long>>> adjacencyList = new HashMap<>();//<Unique, LinkedList<Unique, distance>> / immutable
        HashMap<String, String> match = new HashMap<>();//<Unique, station> / immutable
        HashMap<String, String> revmatch = new HashMap<>();//<station, Unique> / immutable
        HashMap<String, LinkedList<String>> transfer = new HashMap<>();//<station, LinkedLIst<Unique>>
        SortedSet<Pair<Long, String>> accudis = new TreeSet<>();//<accumulated distance, Unique> / starting point is 1. get real result by minus 1 from result / must reset each case...so make this immutable

        HashSet<String> chT = new HashSet<>();//<Unique> transfer stations are in it
        HashMap<String, Pair<Long, String>> contains;//<Unique, member of accD> / whether accD contains specific Unique or not
        HashMap<String, LinkedList<Pair<String, Long>>> tempAdList;//<Unique, LinkedList<Unique, distance>> / temporary
        HashSet<String> visited = new HashSet<>();//<Unique> / check whether Unique(station) is visited (visited Unique is in it)
        HashSet<String> visiSta = new HashSet<>();//station version of visited
        SortedSet<Pair<Long, String>> accD;//clone of accudis
        HashMap<String, String> comeFrom = new HashMap<>();//<Unique1, Unique2> / Unique1 is comes from Unique2
        File file = new File(args[0]);
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
            BufferedReader data = new BufferedReader(new FileReader(file));

            //pre processing
            while (true) {
                String in = data.readLine();
                if (in.equals(""))
                    break;
                String[] inarr = in.split(" ");
                match.put(inarr[0], inarr[1]);
                String o = revmatch.put(inarr[1], inarr[0]);
                if (o != null) {//if value of revmatch has replaced == station is transfer station
                    for (String i : transfer.get(inarr[1])) {
                        if (adjacencyList.get(i) == null) {
                            LinkedList<Pair<String, Long>> a = new LinkedList<>();
                            a.add(new Pair<>(inarr[0], (long) 5));
                            adjacencyList.put(i, a);
                        } else {
                            adjacencyList.get(i).add(new Pair<>(inarr[0], (long) 5));
                        }
                        if (adjacencyList.get(inarr[0]) == null) {
                            LinkedList<Pair<String, Long>> a = new LinkedList<>();
                            a.add(new Pair<>(i, (long) 5));
                            adjacencyList.put(inarr[0], a);
                        } else {
                            adjacencyList.get(inarr[0]).add(new Pair<>(i, (long) 5));
                        }
                    }
                    transfer.get(inarr[1]).add(inarr[0]);
                } else {
                    LinkedList<String> a = new LinkedList<>();
                    a.add(inarr[0]);
                    transfer.put(inarr[1], a);
                }
            }
            while (true) {
                String in = data.readLine();
                if (in == null)
                    break;
                String[] inarr = in.split(" ");
                if (adjacencyList.get(inarr[0]) == null) {
                    LinkedList<Pair<String, Long>> a = new LinkedList<>();
                    a.add(new Pair<>(inarr[1], Long.parseLong(inarr[2])));
                    adjacencyList.put(inarr[0], a);
                } else {
                    adjacencyList.get(inarr[0]).add(new Pair<>(inarr[1], Long.parseLong(inarr[2])));
                }
            }


            while (true) {//every case
                chT = new HashSet<>();
                contains = new HashMap<>();
                visiSta = new HashSet<>();
                visited = new HashSet<>();
                comeFrom = new HashMap<>();
                accD = (TreeSet<Pair<Long, String>>) ((TreeSet<Pair<Long, String>>) accudis).clone();
                tempAdList = (HashMap<String, LinkedList<Pair<String, Long>>>) adjacencyList.clone();

                //interpreting
                String in = br.readLine();
                if (in.equals("QUIT"))
                    break;
                String[] inarr = in.split(" ");
                String start = revmatch.get(inarr[0]);
                String end = revmatch.get(inarr[1]);
                accD.add(new Pair<>((long) 1, start));
                if (transfer.get(inarr[0]).size() > 1) {//if starting station is transfer station
                    for (String i : transfer.get(inarr[0])) {
                        for (String j : transfer.get(inarr[0])) {
                            tempAdList.get(i).remove(new Pair<>(j, (long) 5));
                            tempAdList.get(i).add(new Pair<>(j, (long) 0));
                        }
                    }
                }

                //dijkstra
                while (!visiSta.contains(match.get(end))) {
                    Pair<Long, String> min = accD.first();
                    accD.remove(accD.first());
                    visited.add(min.second());
                    visiSta.add(match.get(min.second()));
                    for (Pair<String, Long> ele : tempAdList.get(min.second())) {
                        if (!visited.contains(ele.first())) {
                            Pair<Long, String> o = contains.get(ele.first());
                            Pair<Long, String> n = new Pair<>((min.first() + ele.second()), ele.first());
                            if (o == null) {
                                accD.add(n);
                                contains.put(ele.first(), new Pair<>(ele.second(), ele.first()));
                                comeFrom.put(ele.first(), min.second());
                            } else if (o.compareTo(n) > 0) {
                                accD.remove(o);
                                contains.remove(ele.first());
                                accD.add(n);
                                contains.put(ele.first(), n);
                                comeFrom.put(ele.first(), min.second());
                            }
                        }
                    }
                }

                //print
                StringBuilder out = new StringBuilder();
                String where = end;//Unique
                String prewhere = null;
                int prestart;
                while (!match.get(where).equals(match.get(start))) {
                    prestart = match.get(where).length() + 1;
                    if (where.equals(end)) {
                        out.insert(0, match.get(where));
                    } else {
                        if (!match.get(where).equals(match.get(prewhere)))
                            out.insert(0, " ").insert(0, match.get(where));
                        else if(!match.get(where).equals(match.get(end))) {
                            out.delete(0, prestart);
                            out.insert(0, " ").insert(0, "]").insert(0, match.get(where)).insert(0, "[");
                        }
                    }
                    prewhere = where;
                    where = comeFrom.get(where);
                }
                out.insert(0, " ").insert(0, match.get(where));
                System.out.println(out.toString());
                System.out.println(accD.first().first() - 1);

                //refresh
                tempAdList.clear();
                contains.clear();
                visited.clear();
                accD.clear();
                comeFrom.clear();
                chT.clear();
            }
        } catch (IOException e) {
            System.out.println("ERROR : " + e.toString());
        }
    }
}
