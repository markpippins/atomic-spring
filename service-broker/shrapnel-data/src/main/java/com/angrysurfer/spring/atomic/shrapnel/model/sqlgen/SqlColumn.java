package com.angrysurfer.spring.atomic.shrapnel.model.sqlgen;

import lombok.Getter;
import lombok.Setter;

import com.angrysurfer.spring.atomic.shrapnel.model.db.DBFieldType;

import jakarta.persistence.*;

@Getter
@Setter
@Entity
@jakarta.persistence.Table(name = "qbe_column", schema = "shrapnel")
public class SqlColumn {

	@ManyToOne
	@JoinColumn(name = "field_type_id")
	public DBFieldType fieldType;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@jakarta.persistence.Column(name = "id", nullable = false)
	private Long id;

	@jakarta.persistence.Column(name = "name", nullable = false)
	private String name;

	@ManyToOne
	@JoinColumn(name = "table_id")
	private SqlTable table;

	@jakarta.persistence.Column(name = "field_index", nullable = false)
	private Integer index;

}
