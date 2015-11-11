package pl.p.lodz.iis.hr.repositories;


import javax.annotation.Resource;

@Resource
public interface FindByNameProvider<T> {
    
    T findByName(String name);
}
