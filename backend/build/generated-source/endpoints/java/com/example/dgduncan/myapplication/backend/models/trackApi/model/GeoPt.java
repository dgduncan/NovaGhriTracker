/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://github.com/google/apis-client-generator/
 * (build: 2016-05-27 16:00:31 UTC)
 * on 2016-06-28 at 18:46:11 UTC 
 * Modify at your own risk.
 */

package com.example.dgduncan.myapplication.backend.models.trackApi.model;

/**
 * Model definition for GeoPt.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the trackApi. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class GeoPt extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Float latitude;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Float longitude;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Float getLatitude() {
    return latitude;
  }

  /**
   * @param latitude latitude or {@code null} for none
   */
  public GeoPt setLatitude(java.lang.Float latitude) {
    this.latitude = latitude;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Float getLongitude() {
    return longitude;
  }

  /**
   * @param longitude longitude or {@code null} for none
   */
  public GeoPt setLongitude(java.lang.Float longitude) {
    this.longitude = longitude;
    return this;
  }

  @Override
  public GeoPt set(String fieldName, Object value) {
    return (GeoPt) super.set(fieldName, value);
  }

  @Override
  public GeoPt clone() {
    return (GeoPt) super.clone();
  }

}
