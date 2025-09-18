package com.paycanvas.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * 店舗マスターエンティティクラス。
 *
 * <p>このクラスは企業が管理する店舗情報を管理するエンティティです。
 * データベースの「m_stores」テーブルにマッピングされ、
 * 店舗の基本情報、所在地、業態種別などを格納します。</p>
 *
 * <p>主な用途：</p>
 * <ul>
 *   <li>店舗情報の一元管理</li>
 *   <li>従業員の所属店舗管理</li>
 *   <li>店舗別売上・給与集計</li>
 *   <li>チェーン店の統合管理</li>
 * </ul>
 */
@Entity
@Table(name = "m_stores")
public class Store {
  /**
   * 店舗ID（主キー）。
   * データベースにて自動採番される一意の識別子です。
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /**
   * 所属企業。
   * この店舗を管理する企業への参照です。
   * 一つの企業が複数の店舗を持つことができます。
   */
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "company_id", nullable = false)
  private Company company;

  /**
   * 店舗名。
   * 店舗の正式名称です。
   * 従業員管理や売上集計時の表示に使用されます。
   */
  @Column(nullable = false)
  private String name;

  /**
   * 店舗種別。
   * 店舗の業態や種別を表す文字列です（例：「本店」「支店」「アウトレット」など）。
   */
  @Column(name = "store_type")
  private String storeType;

  /**
   * 店舗住所。
   * 店舗の所在地住所を格納します。
   * 従業員への連絡や給与明細などで使用される可能性があります。
   */
  @Column(name = "address")
  private String address;

  public Integer getId() {
    return id;
  }

  public Company getCompany() {
    return company;
  }

  public void setCompany(Company company) {
    this.company = company;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getStoreType() {
    return storeType;
  }

  public void setStoreType(String storeType) {
    this.storeType = storeType;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }
}
