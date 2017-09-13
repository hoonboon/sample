package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Pet
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2017-09-12T13:44:46.316Z")

public class Pet   {
	@JsonProperty("id")
	private Long id = null;

	@JsonProperty("name")
	private String name = null;

	@JsonProperty("tag")
	private String tag = null;

	public Pet id(Long id) {
		this.id = id;
		return this;
	}

	public Pet(Long id, String name, String tag) {
		super();
		this.id = id;
		this.name = name;
		this.tag = tag;
	}



	/**
	 * Get id
	 * @return id
	 **/
	@ApiModelProperty(required = true, value = "")
	@NotNull


	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Pet name(String name) {
		this.name = name;
		return this;
	}

	/**
	 * Get name
	 * @return name
	 **/
	@ApiModelProperty(required = true, value = "")
	@NotNull


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Pet tag(String tag) {
		this.tag = tag;
		return this;
	}

	/**
	 * Get tag
	 * @return tag
	 **/
	@ApiModelProperty(value = "")


	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}


	@Override
	public boolean equals(java.lang.Object o) {
		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Pet pet = (Pet) o;
		return Objects.equals(this.id, pet.id) &&
				Objects.equals(this.name, pet.name) &&
				Objects.equals(this.tag, pet.tag);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, name, tag);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("class Pet {\n");

		sb.append("    id: ").append(toIndentedString(id)).append("\n");
		sb.append("    name: ").append(toIndentedString(name)).append("\n");
		sb.append("    tag: ").append(toIndentedString(tag)).append("\n");
		sb.append("}");
		return sb.toString();
	}

	/**
	 * Convert the given object to string with each line indented by 4 spaces
	 * (except the first line).
	 */
	private String toIndentedString(java.lang.Object o) {
		if (o == null) {
			return "null";
		}
		return o.toString().replace("\n", "\n    ");
	}
}

