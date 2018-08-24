# Whac A Matrix
A visualisation of the Jacobi algorithm 
---
The idea of this program is to see the Jacobi algorithm for matrix diagonalization as well as the Gerschgorin circles in action.

The Jacobi algorithm iteratively applies orthogonal operations (Givens rotations) to a symmetric matrix which leads to the decomposition ![decomposition](http://www.sciweavers.org/tex2img.php?eq=A%20%3D%20R%5E%5Ctop%20D%20R&bc=White&fc=Black&im=png&fs=12&ff=cmbright&edit=0) with *R* orthogonal and ![diagonal](http://www.sciweavers.org/tex2img.php?eq=D%20%3D%20diag%28%5Clambda_1%2C%5Cldots%2C%5Clambda_n%29&bc=White&fc=Black&im=png&fs=12&ff=cmbright&edit=0) and ![eigen](http://www.sciweavers.org/tex2img.php?eq=%5Clambda_1%2C%5Cldots%2C%5Clambda_n&bc=White&fc=Black&im=png&fs=12&ff=cmbright&edit=0) are the eigenvalues of *A*.

In each iteration of the Jacobi algorithm a non-diagonal element is chosen to be set to zero. The fact that it is not guranteed that this element stays zero in future iterations has inspired the name of this program. Just like the moles in [Whac A Mole](https://en.wikipedia.org/wiki/Whac-A-Mole), the non-diagonal elements of the matrix will pop up again. But the Jacobi algorithm converges quiet fast, hence you won't have to hit the same entry too often.

The Gerschgorin circles are a neat way to display the "progress of diagonalization". For every row in the matrix, the Gerschgorin circle has its midpoint in this row's diagonal element and its radius is the sum of the absolute values of all non-diagonal elements in this row.

You can also convince yourself that the transformation is truly orthogonal - at least partly. Since geometric visualizations are limited to three dimensions, we can only show the effect of the transformation matrix in 3D. But it's something. In the tab "Orthogonal" you can see a 3D text (which you can choose yourself) that has been transformed using the current transformation matrix (the product of all Givens rotations so far). It is artifically extended to *n* dimensions when you work on a *n x n* matrix (by adding zeros in the other dimensions) and then projected into three dimensions (by leaving away the other dimensions) and again projected into two dimensions (by orthogonal projection). Therefore, the displayed lengths in 3D may vary although the *n* dimensional lengths are preserved by the transformation. But you can indeed see the angles being preserved.
You can choose whether or not you want do

. display what entries changed a lot in a Jacobi step (this nicely demonstrates the effect of the Givens rotation), 
. display the development of the midpoints of the Gerschgorin circles (i.e. the diagonal elements), as well as their diameters
. display the transformed text with its faces or only the edges.
