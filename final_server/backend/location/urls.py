from django.urls import path, include
from location import views

from rest_framework import routers

router = routers.DefaultRouter()
router.register('',views.LocationViewSet)
from . import views

urlpatterns = [
    path('location_list/', views.location_list),
    path('', include(router.urls)),
    path('', include('rest_framework.urls', namespace='rest_framework_category')),
    path('<int:pk>/', views.location_change),
]