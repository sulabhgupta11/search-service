package com.team6.apps.search.model;

import io.swagger.annotations.ApiModelProperty;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;

import java.util.ArrayList;
import java.util.List;


public class ProductSearchParameter {

	@ApiModelProperty(value = "search text", example = "vivo")
	private String searchText;

	@ApiModelProperty(value = "size", example = "100")
	private Integer size = 100;

	@ApiModelProperty(dataType = "List", value="filter(s) to search om")
	private List<Filter> filters = new ArrayList<>();


	public String getSearchText() {
		return searchText;
	}

	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}

	public Integer getSize() {
		return size;
	}

	public void setSize(Integer size) {
		this.size = size;
	}


	public List<Filter> getFilters() {
		return filters;
	}

	public void setFilters(List<Filter> filters) {
		this.filters = filters;
	}


	public static final class Filter {
		@ApiModelProperty(value = "price", example = "price")
		private String key;
		@ApiModelProperty(value = "vivo", example = "vivo")
		private String value;
		@ApiModelProperty(value = "5000", example = "5000")
		private Integer from;
		@ApiModelProperty(value = "15000", example = "15000")
		private Integer to;
		@ApiModelProperty(value = "range", example = "range")
		private String type;

		public String getType() {
			return type;
		}

		public Integer getFrom() {
			return from;
		}

		public void setFrom(Integer from) {
			this.from = from;
		}

		public Integer getTo() {
			return to;
		}

		public void setTo(Integer to) {
			this.to = to;
		}

		public String getKey() {
			return key;
		}

		public String getValue() {
			return value;
		}

		public QueryBuilder toQuery() {
			if ("term".equals(type)) {
				return QueryBuilders.termQuery(this.key + ".keyword", this.value);
			} else if ("range".equals(type)) {
				return createRangeQueryBuilder(key, from, to);
			} else {
				throw new RuntimeException("Unknown type: " + type);
			}
		}

		private RangeQueryBuilder createRangeQueryBuilder(String name, Integer from, Integer to) {
			RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery(name);
			if (from != null) {
				rangeQueryBuilder.from(from);
			}
			if (to != null) {
				rangeQueryBuilder.to(to);
			}
			return rangeQueryBuilder;
		}
	}
}
