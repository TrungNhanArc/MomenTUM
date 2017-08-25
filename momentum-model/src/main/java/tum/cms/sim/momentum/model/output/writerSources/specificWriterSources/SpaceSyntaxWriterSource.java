/*******************************************************************************
 * Welcome to the pedestrian simulation framework MomenTUM. 
 * This file belongs to the MomenTUM version 2.0.2.
 * 
 * This software was developed under the lead of Dr. Peter M. Kielar at the
 * Chair of Computational Modeling and Simulation at the Technical University Munich.
 * 
 * All rights reserved. Copyright (C) 2017.
 * 
 * Contact: peter.kielar@tum.de, https://www.cms.bgu.tum.de/en/
 * 
 * Permission is hereby granted, free of charge, to use and/or copy this software
 * for non-commercial research and education purposes if the authors of this
 * software and their research papers are properly cited.
 * For citation information visit:
 * https://www.cms.bgu.tum.de/en/31-forschung/projekte/456-momentum
 * 
 * However, further rights are not granted.
 * If you need another license or specific rights, contact us!
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 ******************************************************************************/

package tum.cms.sim.momentum.model.output.writerSources.specificWriterSources;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.converters.reflection.ReflectionConverter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

import tum.cms.sim.momentum.data.layout.ScenarioManager;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.output.writerSources.genericWriterSources.SingleElementWriterSource;
import tum.cms.sim.momentum.utility.generic.Unique;
import tum.cms.sim.momentum.utility.lattice.CellIndex;
import tum.cms.sim.momentum.utility.lattice.ILattice;
import tum.cms.sim.momentum.utility.spaceSyntax.DepthMap;
import tum.cms.sim.momentum.utility.spaceSyntax.DepthMapSubArea;
import tum.cms.sim.momentum.utility.spaceSyntax.SpaceSyntax;

public class SpaceSyntaxWriterSource extends SingleElementWriterSource {
	
	private ScenarioManager scenarioManager = null;
	private XStream xStream = null;

	public void setScenarioManager(ScenarioManager scenarioManager) {
		
		this.scenarioManager = scenarioManager;
	}

	@Override
	public String readSingleValue(String outputTypeName) {

		String xml = this.xStream.toXML(this.scenarioManager.getSpaceSyntax());
		
		return xml;
	}

	@Override
	public void initialize(SimulationState simulationState) {
		
		this.dataItemNames.add("adding fake key");
		this.xStream = new XStream();
		this.createAliasesForClasses(this.xStream, scenarioManager.getSpaceSyntax().getDepthMap().getLattice());
		
	}

	private void createAliasesForClasses(XStream xStream, ILattice lattice) {
		
		xStream.alias("SpaceSyntax", SpaceSyntax.class);
		xStream.omitField(SpaceSyntax.class, "properties");
		xStream.aliasField("DepthMap", SpaceSyntax.class, "depthMap");

		xStream.alias("DepthMap", DepthMap.class);
		xStream.omitField(DepthMap.class, "lattice");
		xStream.addImplicitCollection(DepthMap.class, "disconnectedAreas");
		xStream.useAttributeFor(DepthMap.class, "domainRows");
		xStream.useAttributeFor(DepthMap.class, "domainColumns");
		xStream.useAttributeFor(DepthMap.class, "maxX");
		xStream.useAttributeFor(DepthMap.class, "minX");
		xStream.useAttributeFor(DepthMap.class, "maxY");
		xStream.useAttributeFor(DepthMap.class, "minY");
		
		xStream.useAttributeFor(Unique.class, "id");
		xStream.useAttributeFor(Unique.class, "name");
		
		xStream.alias("DepthMapSubArea", DepthMapSubArea.class);
		xStream.addImplicitCollection(DepthMapSubArea.class, "connectedIndices");
		xStream.useAttributeFor(DepthMapSubArea.class, "minimum");
		xStream.useAttributeFor(DepthMapSubArea.class, "maximum");
		
		xStream.alias("CellIndex", CellIndex.class);
		xStream.omitField(CellIndex.class, "index");

		xStream.registerConverter(new ReflectionConverter(xStream.getMapper(), xStream.getReflectionProvider()) {
			
			@SuppressWarnings("rawtypes")
			@Override
			public boolean canConvert(Class clazz) {

				return clazz.equals(CellIndex.class);
			}
			
			@Override
			public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
				// unnecessary to add implementation
				return super.unmarshal(reader, context);
			}
			
			@Override
			public void marshal(Object source, HierarchicalStreamWriter writer, MarshallingContext context) {
				
				CellIndex cellIndex = (CellIndex) source;
				
				writer.addAttribute("y", "" + cellIndex.getRow());
				writer.addAttribute("x", "" + cellIndex.getColumn());
				writer.addAttribute("value", "" + lattice.getCellNumberValue(cellIndex));
				writer.close();
				
			}
		}, 5000);
	}
}
