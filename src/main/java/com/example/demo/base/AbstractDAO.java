package com.example.demo.base;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;
import javax.validation.ValidationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

/**
 * Baseclass for all daos
 * @author udobischof
 *
 * @param <T>
 */
@Repository
@Transactional
public abstract class AbstractDAO<T extends AbstractEntity> {

	@Autowired
	protected EntityManager entityManager;
	
	@SuppressWarnings("unchecked")
	public String getEntityName() {
		Class<T> typeOfT = (Class<T>)
                ((ParameterizedType)getClass()
                .getGenericSuperclass())
                .getActualTypeArguments()[0];
		String simpleName = typeOfT.getSimpleName();
		return simpleName;
	};
	
	public String getEntityName(Class<?> otherEntityClazz) {
		String simpleName = otherEntityClazz.getSimpleName();
		return simpleName;
	}
	
	@SuppressWarnings("unchecked")
	public Class<T> getEntityClazz() {
		Class<T> typeOfT = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
		return typeOfT;
	};

	public void create(T e) {
		Set<String> validationResult = e.validate();
		if (validationResult.size() > 0) {
			throw new ValidationException("Validation failed: " + StringUtils.collectionToCommaDelimitedString(validationResult));
		}
		entityManager.persist(e);
	}
	
	public List<T> findAll() {
		return entityManager.createQuery("select e from " + this.getEntityName() + " e", getEntityClazz()).getResultList();
	}
	
	public T findById(long id) {
		try {
			TypedQuery<T> q = entityManager.createQuery("select e from " + this.getEntityName() + " e where id=:id", getEntityClazz());
			q.setParameter("id", id);
			return q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}
	
	public void deleteAll() {
		entityManager.createQuery("delete from " + this.getEntityName() + " e").executeUpdate();
	}
	
	public T update(T e) {
		return entityManager.merge(e);
	}
	
	public void flush() {
		this.entityManager.flush();
	}
	
}
