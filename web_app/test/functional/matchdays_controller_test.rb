require 'test_helper'

class MatchdaysControllerTest < ActionController::TestCase
  test "should get index" do
    get :index
    assert_response :success
    assert_not_nil assigns(:matchdays)
  end

  test "should get new" do
    get :new
    assert_response :success
  end

  test "should create matchday" do
    assert_difference('Matchday.count') do
      post :create, :matchday => { }
    end

    assert_redirected_to matchday_path(assigns(:matchday))
  end

  test "should show matchday" do
    get :show, :id => matchdays(:one).to_param
    assert_response :success
  end

  test "should get edit" do
    get :edit, :id => matchdays(:one).to_param
    assert_response :success
  end

  test "should update matchday" do
    put :update, :id => matchdays(:one).to_param, :matchday => { }
    assert_redirected_to matchday_path(assigns(:matchday))
  end

  test "should destroy matchday" do
    assert_difference('Matchday.count', -1) do
      delete :destroy, :id => matchdays(:one).to_param
    end

    assert_redirected_to matchdays_path
  end
end
