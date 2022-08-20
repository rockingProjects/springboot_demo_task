package com.example.demo.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.Valid;
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
@Table(name = "person")
@Getter
@Setter
@Builder(setterPrefix = "with")
@NoArgsConstructor
@AllArgsConstructor
public class PersonEntity extends AbstractEntity {
	
	private static final long serialVersionUID = 8187026921073225975L;
	
	public static final String IDENTIFIER = "identifier";
	@Column(name = "identifier", nullable = false, unique = true)
	@NotNull(message = "PersonEntity.identifier:NotNull")
	@Length(min = 20, max = 20)
	private String identifier;
	
	@NotNull(message = "PersonEntity.sex:NotNull")
	private SexEnum sex;
	@NotEmpty(message = "PersonEntity.firstName:NotEmpty")
	private String firstName;
	@NotEmpty(message = "PersonEntity.lastName:NotEmpty")
	private String lastName;
	
	@Valid
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "kingdom_ID", nullable = false)
	private KingdomEntity kingdom;
}
