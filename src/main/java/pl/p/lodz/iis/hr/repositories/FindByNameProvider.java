package pl.p.lodz.iis.hr.repositories;

@FunctionalInterface
public interface FindByNameProvider<T> {

    T findByName(String name);
}
