/*
 *  Copyright 2015, 2016 Jiri Lidinsky
 *
 *  This file is part of control4j.
 *
 *  control4j is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, version 3.
 *
 *  control4j is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with control4j.  If not, see <http://www.gnu.org/licenses/>.
 */

package control4j.application.nativelang;

import static org.apache.commons.collections4.CollectionUtils.unmodifiableCollection;
import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import cz.lidinsky.tools.CommonException;
import cz.lidinsky.tools.ExceptionCode;

import java.util.ArrayList;
import java.util.Collection;

import cz.lidinsky.tools.ToStringBuilder;
import java.util.List;
import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 *
 * Stands for a module element.
 *
 */
public class Module extends DescriptionBase {

    private String className;
    private List<Resource> resources;
    private List<Input> input;
    private List<Output> output;
    private List<String> inputTags;
    private List<String> outputTags;

    /**
     *  An empty constructor.
     */
    public Module() { }

    /**
     *  Creates an empty module object.
     *
     *  @param className a name of the class that implements functionality of a
     *             module
     */
    public Module(String className) {
        this.className = className;
    }

    /**
     *  Returns name of the class that implements the functionality of the
     *  module.
     *
     *  @return name of the class that implements the functionality of the
     *             module.
     *
     *  @throws CommonException
     *             if the class name is eather null or empty
     */
    public String getClassName() {
        return className;
    }

    Module setClassName(String className) {
        this.className = className;
        return this;
    }


    void add(Resource resource) {
        resources = addNotNull(resource, resources);
    }

    protected static <T> List<T> addNotNull(T item, List<T> list) {
        if (item != null) {
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(item);
        }
        return list;
    }

    public Collection<Resource> getResources() {
        return unmodifiableCollection(emptyIfNull(resources));
    }


    void add(Input input) {
        this.input = addNotNull(input, this.input);
    }

    public Collection<Input> getInput() {
        return unmodifiableCollection(emptyIfNull(input));
    }


    void add(Output output) {
        this.output = addNotNull(output, this.output);
    }

    public Collection<Output> getOutput() {
        return unmodifiableCollection(emptyIfNull(output));
    }

    void addInputTag(String tag) {
        this.inputTags = addNotNull(tag, this.inputTags);
    }

    public Collection<String> getInputTags() {
        return unmodifiableCollection(emptyIfNull(inputTags));
    }


    void addOutputTag(String tag) {
        this.outputTags = addNotNull(tag, this.outputTags);
    }

    public Collection<String> getOutputTags() {
        return unmodifiableCollection(emptyIfNull(outputTags));
    }


    protected void check() {
        CommonException e = null;
        if (isBlank(className)) {
            e = new CommonException()
                    .setCode(ExceptionCode.BLANK_ARGUMENT)
                    .set("message", "Class name of the module may not be blank!");
        }
        if (e != null) {
            throw e;
        }
    }

    @Override
    public void toString(ToStringBuilder builder) {
        super.toString(builder);
        builder.append("className", className)
                .append("resources", resources)
                .append("input", input)
                .append("output", output)
                .append("inputTags", inputTags)
                .append("outputTags", outputTags);
    }

}
