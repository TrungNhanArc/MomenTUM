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

package tum.cms.sim.momentum.model.tactical.routing.unifiedRoutingModel.underylingModels;

import java.util.Collection;
import java.util.HashMap;

import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.data.agent.pedestrian.types.ITacticalPedestrian;
import tum.cms.sim.momentum.infrastructure.execute.SimulationState;
import tum.cms.sim.momentum.model.support.perceptional.PerceptionalModel;
import tum.cms.sim.momentum.model.tactical.routing.unifiedRoutingModel.UnifiedRoutingConstant;
import tum.cms.sim.momentum.utility.graph.Edge;
import tum.cms.sim.momentum.utility.graph.Graph;
import tum.cms.sim.momentum.utility.graph.Vertex;

public class FastestPathCalculator extends UnifiedSocialCalculator {

	protected ITacticalPedestrian currentPedestrian = null;
	
	public ITacticalPedestrian getCurrentPedestrian() {
		return currentPedestrian;
	}

	public void setCurrentPedestrian(ITacticalPedestrian currentPedestrian) {
		this.currentPedestrian = currentPedestrian;
	}

	protected PerceptionalModel currentPerception = null;
	
	public PerceptionalModel getCurrentPerception() {
		return currentPerception;
	}

	public void setCurrentPerception(PerceptionalModel currentPerception) {
		this.currentPerception = currentPerception;
	}
	
	@Override
	public void globalUpdateWeight(Graph graph, Collection<IRichPedestrian> pedestrians, SimulationState simulationState) {
		
		double meanVelocity = 0.0;
		Edge edge = null;
		Double sumSpeed = 0.0;
		Double numberOfPeds = 0.0;
		
		HashMap<Edge, Double> sumPedestriansOnEdge = new HashMap<Edge, Double>(); 
		HashMap<Edge, Double> sumSpeedOnEdge = new HashMap<Edge, Double>(); 
		
		if(pedestrians.size() > 0) {
			
			for(IRichPedestrian pedestrian : pedestrians) {
				
				// new pedestrian could not find a previous node,
				if(pedestrian.getRoutingState() == null || pedestrian.getRoutingState().getLastVisit() == null) {
					
					continue;
				}
				
				if(pedestrian.getRoutingState().getLastVisit().getId() == -1) { // pseudo state
					
					continue;
				}
				
				// on the route from previous to current intermediate vertex
				edge = graph.getEdge(pedestrian.getRoutingState().getLastVisit(),
						pedestrian.getRoutingState().getNextVisit());
			
				if(edge == null) { // no edge found? 
					
					continue;
				}
				
				numberOfPeds = sumPedestriansOnEdge.get(edge); 
				numberOfPeds = numberOfPeds == null ? 0.0 : numberOfPeds;
				
				sumPedestriansOnEdge.put(edge, numberOfPeds + 1.0);
				
				sumSpeed = sumSpeedOnEdge.get(edge);
				sumSpeed = sumSpeed == null ? 0.0 : sumSpeed;
				
				sumSpeedOnEdge.put(edge, sumSpeed + pedestrian.getVelocity().getMagnitude());
			}
			
			for(Vertex current : graph.getVertices()) {
				
				for(Edge updateEdge : graph.getSuccessorEdges(current)) {
					
					sumSpeed = sumSpeedOnEdge.get(updateEdge);
					numberOfPeds = sumPedestriansOnEdge.get(updateEdge);
					numberOfPeds = numberOfPeds == null ? 0.0 : numberOfPeds;
					
					if(sumSpeed == null || numberOfPeds == null || sumSpeed < 0 || numberOfPeds <= 0) {
						
						meanVelocity = UnifiedRoutingConstant.FastestMeanSpeed;
					}
					else {
						
						meanVelocity = sumSpeed / numberOfPeds;
					}
					
					updateEdge.setWeight(UnifiedRoutingConstant.FastestEdgeMeanSpeedWeightName, meanVelocity);
				}
			}
		}
	}
	
	@Override
	public double calculateWeight(Graph graph, Vertex previousVisit, 
			Vertex target,
			Vertex current,
			Vertex successor) {

		Edge edge = graph.getEdge(current, successor);

		double meanVelocity = UnifiedRoutingConstant.FastestMeanSpeed;

		//if(currentPerception.isVisible(currentPedestrian.getPosition(), edge)) {
			
			meanVelocity = edge.getWeight(UnifiedRoutingConstant.FastestEdgeMeanSpeedWeightName);
		//}
		
		//double euklideanDistance = current.euklidDistanceBetweenVertex(successor);

		return 1.0 / meanVelocity; //euklideanDistance * (1.0 / meanVelocity) ;
	}
}
