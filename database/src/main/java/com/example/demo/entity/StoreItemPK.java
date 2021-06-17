package com.example.demo.entity;

import lombok.EqualsAndHashCode;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import java.io.Serializable;

@EqualsAndHashCode

public class StoreItemPK implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long item;
    private Long store;

    public StoreItemPK() {}

    public StoreItemPK(Long item, Long store) {
        this.item= item;
        this.store= store;
    }

}
