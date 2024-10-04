/*
 * Copyright 2020 EPAM Systems
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.epam.ta.reportportal.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.epam.ta.reportportal.entity.ItemAttribute;
import com.epam.ta.reportportal.ws.reporting.ItemAttributeResource;
import com.epam.ta.reportportal.ws.reporting.BulkInfoUpdateRQ;
import com.epam.ta.reportportal.ws.reporting.UpdateItemAttributeRQ;
import com.epam.reportportal.rules.exception.ReportPortalException;
import com.google.common.collect.Lists;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.HashSet;
import org.junit.jupiter.api.Test;

/**
 * @author <a href="mailto:ihar_kahadouski@epam.com">Ihar Kahadouski</a>
 */
class ItemInfoUtilsTest {

  @Test
  void nullAttributesCollectionTest() {
    Optional<ItemAttribute> attribute = ItemInfoUtils.extractAttribute(null, "key");
    assertTrue(attribute.isEmpty());
  }

  @Test
  void emptyAttributesCollectionTest() {
    Optional<ItemAttribute> attribute = ItemInfoUtils.extractAttribute(Collections.emptyList(), "key");
    assertTrue(attribute.isEmpty());
  }

  @Test
  void shouldFindNonSystemAttribute() {
    String key = "key1";
    Optional<ItemAttribute> attribute = ItemInfoUtils.extractAttribute(getAttributes(), key);
    assertTrue(attribute.isPresent());
    assertEquals(key, attribute.get().getKey());
  }

  @Test
  void shouldFindSystemAttribute() {
    String key = "key3";
    Optional<ItemAttribute> attribute = ItemInfoUtils.extractAttribute(getAttributes(), key);
    assertTrue(attribute.isPresent());
    assertEquals(key, attribute.get().getKey());
  }

  @Test
  void shouldNotFindAttribute() {
    String key = "not-exist";
    Optional<ItemAttribute> attribute = ItemInfoUtils.extractAttribute(getAttributes(), key);
    assertTrue(attribute.isEmpty());
  }

  @Test
  void nullAttributeResourceCollectionTest() {
    Optional<ItemAttributeResource> itemAttributeResource = ItemInfoUtils.extractAttributeResource(null, "key");
    assertTrue(itemAttributeResource.isEmpty());
  }

  @Test
  void emptyAttributeResourcesCollectionTest() {
    Optional<ItemAttributeResource> itemAttributeResource = ItemInfoUtils.extractAttributeResource(Collections.emptyList(), "key");
    assertTrue(itemAttributeResource.isEmpty());
  }

  @Test
  void shouldFindAttributeResource() {
    String key = "key1";
    Optional<ItemAttributeResource> itemAttributeResource = ItemInfoUtils.extractAttributeResource(getAttributeResources(), key);
    assertTrue(itemAttributeResource.isPresent());
    assertEquals(key, itemAttributeResource.get().getKey());
  }

  @Test
  void shouldNotFindAttributeResource() {
    String key = "not-exist";
    Optional<ItemAttributeResource> itemAttributeResource = ItemInfoUtils.extractAttributeResource(getAttributeResources(), key);
    assertTrue(itemAttributeResource.isEmpty());
  }

  @Test
  void updateDescriptionTest() {
    BulkInfoUpdateRQ.Description description = new BulkInfoUpdateRQ.Description();
    description.setAction(BulkInfoUpdateRQ.Action.UPDATE);
    description.setComment("new comment");
    Optional<String> updatedDescription = ItemInfoUtils.updateDescription(description, "existing description");
    assertTrue(updatedDescription.isPresent());
    assertEquals("existing description new comment", updatedDescription.get());
  }

  @Test
  void createDescriptionTest() {
    BulkInfoUpdateRQ.Description description = new BulkInfoUpdateRQ.Description();
    description.setAction(BulkInfoUpdateRQ.Action.CREATE);
    description.setComment("new comment");
    Optional<String> updatedDescription = ItemInfoUtils.updateDescription(description, null);
    assertTrue(updatedDescription.isPresent());
    assertEquals("new comment", updatedDescription.get());
  }

  @Test
  void emptyDescriptionTest() {
    BulkInfoUpdateRQ.Description description = new BulkInfoUpdateRQ.Description();
    description.setAction(BulkInfoUpdateRQ.Action.CREATE);
    Optional<String> updatedDescription = ItemInfoUtils.updateDescription(description, null);
    assertTrue(updatedDescription.isEmpty());
  }

  @Test
  void findAttributeByResourceTest() {
    Set<ItemAttribute> attributes = new HashSet<>(getAttributes());
    ItemAttributeResource resource = new ItemAttributeResource("key1", "value1");
    ItemAttribute attribute = ItemInfoUtils.findAttributeByResource(attributes, resource);
    assertEquals("key1", attribute.getKey());
    assertEquals("value1", attribute.getValue());
  }

  @Test
  void findAttributeByResourceExceptionTest() {
    Set<ItemAttribute> attributes = new HashSet<>(getAttributes());
    ItemAttributeResource resource = new ItemAttributeResource("key4", "value4");
    assertThrows(ReportPortalException.class, () -> ItemInfoUtils.findAttributeByResource(attributes, resource));
  }

  @Test
  void updateAttributeTest() {
    Set<ItemAttribute> attributes = new HashSet<>(getAttributes());
    UpdateItemAttributeRQ updateItemAttributeRQ = new UpdateItemAttributeRQ();
    updateItemAttributeRQ.setFrom(new ItemAttributeResource("key1", "value1"));
    updateItemAttributeRQ.setTo(new ItemAttributeResource("key1", "newValue"));
    ItemInfoUtils.updateAttribute(attributes, updateItemAttributeRQ);
    Optional<ItemAttribute> updatedAttribute = attributes.stream().filter(attr -> "key1".equals(attr.getKey())).findAny();
    assertTrue(updatedAttribute.isPresent());
    assertEquals("newValue", updatedAttribute.get().getValue());
  }

  @Test
  void updateAttributeExceptionTest() {
    Set<ItemAttribute> attributes = new HashSet<>(getAttributes());
    UpdateItemAttributeRQ updateItemAttributeRQ = new UpdateItemAttributeRQ();
    updateItemAttributeRQ.setFrom(new ItemAttributeResource("key4", "value4"));
    updateItemAttributeRQ.setTo(new ItemAttributeResource("key4", "newValue"));
    assertThrows(ReportPortalException.class, () -> ItemInfoUtils.updateAttribute(attributes, updateItemAttributeRQ));
  }

  @Test
  void containsAttributeTest() {
    Set<ItemAttribute> attributes = new HashSet<>(getAttributes());
    ItemAttributeResource resource = new ItemAttributeResource("key1", "value1");
    boolean contains = ItemInfoUtils.containsAttribute(attributes, resource);
    assertTrue(contains);
  }

  @Test
  void doesNotContainAttributeTest() {
    Set<ItemAttribute> attributes = new HashSet<>(getAttributes());
    ItemAttributeResource resource = new ItemAttributeResource("key4", "value4");
    boolean contains = ItemInfoUtils.containsAttribute(attributes, resource);
    assertTrue(!contains);
  }

  private List<ItemAttribute> getAttributes() {
    return Lists.newArrayList(
        new ItemAttribute("key1", "value1", false),
        new ItemAttribute("key2", "value2", false),
        new ItemAttribute("key3", "value3", true)
    );
  }

  private List<ItemAttributeResource> getAttributeResources() {
    return Lists.newArrayList(
        new ItemAttributeResource("key1", "value1"),
        new ItemAttributeResource("key2", "value2")
    );
  }
}