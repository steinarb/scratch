/*
 * Copyright 2024 Steinar Bang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations
 * under the License.
 */
package no.priv.bang.ratatoskr.asvocabulary;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public final class LinkOrObjectList implements LinkOrObject, List<LinkOrObject> {
    List<LinkOrObject> impl;

    public LinkOrObjectList(List<LinkOrObject> impl) {
        super();
        this.impl = impl;
    }

    @Override
    public Object context() {
        return null;
    }

    @Override
    public ActivityStreamObjectType type() {
        return null;
    }

    @Override
    public int size() {
        return impl.size();
    }

    @Override
    public boolean isEmpty() {
        return impl.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
        return impl.contains(o);
    }

    @Override
    public Iterator<LinkOrObject> iterator() {
        return impl.iterator();
    }

    @Override
    public Object[] toArray() {
        return impl.toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return impl.toArray(a);
    }

    @Override
    public boolean add(LinkOrObject e) {
        return impl.add(e);
    }

    @Override
    public boolean remove(Object o) {
        return impl.remove(o);
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return impl.containsAll(c);
    }

    @Override
    public boolean addAll(Collection<? extends LinkOrObject> c) {
        return impl.addAll(c);
    }

    @Override
    public boolean addAll(int index, Collection<? extends LinkOrObject> c) {
        return impl.addAll(index, c);
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        return impl.removeAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return impl.retainAll(c);
    }

    @Override
    public void clear() {
        impl.clear();
    }

    @Override
    public LinkOrObject get(int index) {
        return impl.get(index);
    }

    @Override
    public LinkOrObject set(int index, LinkOrObject element) {
        return impl.set(index, element);
    }

    @Override
    public void add(int index, LinkOrObject element) {
        impl.add(index, element);
    }

    @Override
    public LinkOrObject remove(int index) {
        return impl.remove(index);
    }

    @Override
    public int indexOf(Object o) {
        return impl.indexOf(o);
    }

    @Override
    public int lastIndexOf(Object o) {
        return impl.lastIndexOf(o);
    }

    @Override
    public ListIterator<LinkOrObject> listIterator() {
        return impl.listIterator();
    }

    @Override
    public ListIterator<LinkOrObject> listIterator(int index) {
        return impl.listIterator(index);
    }

    @Override
    public List<LinkOrObject> subList(int fromIndex, int toIndex) {
        return impl.subList(fromIndex, toIndex);
    }

}
