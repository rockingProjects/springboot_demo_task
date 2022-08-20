package com.example.demo.dao;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

import org.springframework.stereotype.Component;

import com.example.demo.base.AbstractDAO;
import com.example.demo.entity.KingdomEntity;

@Component
public class KingdomDao extends AbstractDAO<KingdomEntity> {

	public KingdomEntity findByIdentifier(String kingdomIdentifier) {
		try {
			TypedQuery<KingdomEntity> q = entityManager.createQuery("select e from " + this.getEntityName() + " e where identifier=:identifier", getEntityClazz());
			q.setParameter("identifier", kingdomIdentifier);
			return q.getSingleResult();
		} catch (NoResultException e) {
			return null;
		}
	}

}
