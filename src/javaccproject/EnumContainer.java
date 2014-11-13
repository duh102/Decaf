/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package javaccproject;

/**
 *
 * @author Lenovo
 */
public class EnumContainer<T extends Enum<T>> {
    public T item;
    public EnumContainer(T item) {
        this.item = item;
    }
}
