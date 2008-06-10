package org.londonwicket.osiv.jpa;

import javax.persistence.EntityManager;

public class JpaThreadLocal {

	private static ThreadLocal<EntityManager> local = new ThreadLocal<EntityManager>();

    public static EntityManager get() {
    	return local.get();
    }
    
    public static void set(EntityManager manager){
    	local.set(manager);
    }
    
}
