package Models;

import java.util.Comparator;

/**
 * Created by Solomiia on 5/20/2014.
 */
public class Atmosphera implements Comparable<Atmosphera>{

    public String BankName;

    public String City;

    public String Street;

    public String GroupName;

    public Location Location;

    @Override
    public int compareTo(Atmosphera another) {
        return Comparators.BankName.compare(this, another);
    }

    public static class Comparators {

        public static Comparator<Atmosphera> BankName = new Comparator<Atmosphera>() {
            @Override
            public int compare(Atmosphera o1, Atmosphera o2) {
                return o1.BankName.compareTo(o2.BankName);
            }
        };
        public static Comparator<Atmosphera> City = new Comparator<Atmosphera>() {
            @Override
            public int compare(Atmosphera o1, Atmosphera o2) {
                return o1.City.compareTo(o2.City);
            }
        };
        public static Comparator<Atmosphera> Street = new Comparator<Atmosphera>() {
            @Override
            public int compare(Atmosphera o1, Atmosphera o2) {
                return o1.Street.compareTo(o2.Street);
            }
        };
    }
}
