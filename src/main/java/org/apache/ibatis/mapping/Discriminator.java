/*
 *    Copyright 2009-2012 the original author or authors.
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package org.apache.ibatis.mapping;

import org.apache.ibatis.mapping.ResultMapping;
import org.apache.ibatis.session.Configuration;

import java.util.Collections;
import java.util.Map;

/**
 * @author Clinton Begin
 */
public class Discriminator {

  private org.apache.ibatis.mapping.ResultMapping resultMapping;
  private Map<String, String> discriminatorMap;

  private Discriminator() {
  }

  public static class Builder {
    private org.apache.ibatis.mapping.Discriminator discriminator = new org.apache.ibatis.mapping.Discriminator();

    public Builder(Configuration configuration, org.apache.ibatis.mapping.ResultMapping resultMapping, Map<String, String> discriminatorMap) {
      discriminator.resultMapping = resultMapping;
      discriminator.discriminatorMap = discriminatorMap;
    }

    public org.apache.ibatis.mapping.Discriminator build() {
      assert discriminator.resultMapping != null;
      assert discriminator.discriminatorMap != null;
      assert discriminator.discriminatorMap.size() > 0;
      //lock down map
      discriminator.discriminatorMap = Collections.unmodifiableMap(discriminator.discriminatorMap);
      return discriminator;
    }
  }

  public ResultMapping getResultMapping() {
    return resultMapping;
  }

  public Map<String, String> getDiscriminatorMap() {
    return discriminatorMap;
  }

  public String getMapIdFor(String s) {
    return discriminatorMap.get(s);
  }

}
