package com.example.demo.entity;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.Length;

import com.example.demo.base.AbstractEntity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "kingdom")
@Getter
@Setter
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
public class KingdomEntity extends AbstractEntity {
	
	private static final long serialVersionUID = 5080899426488316999L;
	
	public static final String IDENTIFIER = "identifier";
	@Column(name = "identifier", nullable = false, unique = true)
	@NotNull(message = "KingdomEntity.identifier:NotNull")
	@Length(min = 20, max = 20)
	private String identifier;
	
	@NotEmpty(message = "KingdomEntity.name:NotEmpty")
	private String name;
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "kingdom")
	private List<PersonEntity> residents;
}
