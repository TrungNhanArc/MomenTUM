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

package tum.cms.sim.momentum.model.generator.geometry;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;
import org.apache.commons.math3.util.FastMath;
import tum.cms.sim.momentum.configuration.generator.GeneratorGeometryConfiguration.GeometryGeneratorType;
import tum.cms.sim.momentum.data.agent.pedestrian.types.IRichPedestrian;
import tum.cms.sim.momentum.data.layout.ScenarioManager;
import tum.cms.sim.momentum.data.layout.area.Area;
import tum.cms.sim.momentum.utility.generic.Unique;
import tum.cms.sim.momentum.utility.geometry.Vector2D;
import tum.cms.sim.momentum.utility.probability.HighQualityRandom;

public abstract class GeneratorGeometry extends Unique {

	protected GeometryGeneratorType geometryType = null;
	
	public GeometryGeneratorType getGeometryType() {
		return geometryType;
	}

	protected static Random random =  new HighQualityRandom();
	
	public GeneratorGeometry() { }
	
	public abstract void initialize(ScenarioManager scenarioManager, int scenarioLatticeID, Area area, double safetyDistance, double maximalRadius);
	
	public abstract Collection<Vector2D> calculateFreeSpawnPositions(Collection<IRichPedestrian> allPedestrians, double maximalRadius);

	public Collection<Vector2D> selectFromFreePositions(Collection<Vector2D> foundPositions, int pedestriansToGenerate) {
		
		ArrayList<Vector2D> selectedPositions = new ArrayList<Vector2D>();

		ArrayList<Vector2D> foundPositionsList = new ArrayList<Vector2D>();
		
		foundPositions.forEach(found -> foundPositionsList.add(found));
		
		pedestriansToGenerate = FastMath.min(pedestriansToGenerate, foundPositions.size());
		
		for(int iter = 0; iter < pedestriansToGenerate; iter++) {
			
			int index = random.nextInt(foundPositionsList.size());
			selectedPositions.add(foundPositionsList.get(index));
			foundPositionsList.remove(index);
		}
		
		return selectedPositions;
	}
}
