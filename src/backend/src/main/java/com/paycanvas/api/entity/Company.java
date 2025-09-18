package com.paycanvas.api.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * 企業マスターエンティティクラス。
 *
 * <p>このクラスはシステムを利用する企業（テナント）情報を管理するエンティティです。
 * データベースの「m_companies」テーブルにマッピングされ、
 * 企業の基本情報、連絡先、住所などを格納します。</p>
 *
 * <p>主な用途：</p>
 * <ul>
 *   <li>マルチテナント環境でのテナント管理</li>
 *   <li>企業ごとのデータ分離</li>
 *   <li>契約・課金管理の基盤データ</li>
 *   <li>企業別設定・機能制御</li>
 * </ul>
 */
@Entity
@Table(name = "m_companies")
public class Company {
  /**
   * 企業ID（主キー）。
   * データベースにて自動採番される一意の識別子です。
   */
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  /**
   * 企業名。
   * 企業の正式名称です。
   */
  @Column(nullable = false)
  private String name;

  /**
   * 企業ステータス。
   * 企業の状態を表します（例："ACTIVE", "INACTIVE", "TRIAL"など）。
   * サービス利用の可否判定に使用されます。
   */
  @Column(nullable = false)
  private String status;

  /**
   * 郵便番号。
   * 企業の所在地郵便番号です。
   */
  @Column(name = "postal_code")
  private String postalCode;

  /**
   * 住所。
   * 企業の所在地住所です。
   */
  @Column(name = "address")
  private String address;

  /**
   * 電話番号。
   * 企業の代表電話番号です。
   */
  @Column(name = "phone")
  private String phone;

  /**
   * 担当者名。
   * 企業の担当者氏名です。
   */
  @Column(name = "contact_name")
  private String contactName;

  /**
   * 担当者名（カナ）。
   * 企業の担当者氏名のカタカナ表記です。
   */
  @Column(name = "contact_kana")
  private String contactKana;

  /**
   * 担当者メールアドレス。
   * 企業の担当者の連絡用メールアドレスです。
   */
  @Column(name = "contact_email")
  private String contactEmail;

  public Integer getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getPostalCode() {
    return postalCode;
  }

  public void setPostalCode(String postalCode) {
    this.postalCode = postalCode;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
  }

  public String getPhone() {
    return phone;
  }

  public void setPhone(String phone) {
    this.phone = phone;
  }

  public String getContactName() {
    return contactName;
  }

  public void setContactName(String contactName) {
    this.contactName = contactName;
  }

  public String getContactKana() {
    return contactKana;
  }

  public void setContactKana(String contactKana) {
    this.contactKana = contactKana;
  }

  public String getContactEmail() {
    return contactEmail;
  }

  public void setContactEmail(String contactEmail) {
    this.contactEmail = contactEmail;
  }
}
