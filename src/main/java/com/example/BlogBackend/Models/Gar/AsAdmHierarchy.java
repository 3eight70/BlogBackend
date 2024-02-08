package com.example.BlogBackend.Models.Gar;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "as_adm_hierarchy", schema = "fias")
public class AsAdmHierarchy {
    @Id
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "objectid")
    private Long objectid;

    @Column(name = "parentobjid")
    private Long parentobjid;

    @Column(name = "changeid")
    private Long changeid;

    @Column(name = "regioncode", length = Integer.MAX_VALUE)
    private String regioncode;

    @Column(name = "areacode", length = Integer.MAX_VALUE)
    private String areacode;

    @Column(name = "citycode", length = Integer.MAX_VALUE)
    private String citycode;

    @Column(name = "placecode", length = Integer.MAX_VALUE)
    private String placecode;

    @Column(name = "plancode", length = Integer.MAX_VALUE)
    private String plancode;

    @Column(name = "streetcode", length = Integer.MAX_VALUE)
    private String streetcode;

    @Column(name = "previd")
    private Long previd;

    @Column(name = "nextid")
    private Long nextid;

    @Column(name = "updatedate")
    private LocalDate updatedate;

    @Column(name = "startdate")
    private LocalDate startdate;

    @Column(name = "enddate")
    private LocalDate enddate;

    @Column(name = "isactive")
    private Integer isactive;

    @Column(name = "path", length = Integer.MAX_VALUE)
    private String path;

}